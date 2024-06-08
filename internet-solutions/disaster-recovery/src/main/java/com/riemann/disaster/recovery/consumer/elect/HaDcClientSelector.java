package com.riemann.disaster.recovery.consumer.elect;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import com.riemann.disaster.recovery.config.InoutProperties;

import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 高可用总部客户端选择器
 *
 * @author 微信公众号【老周聊架构】
 */
@Slf4j
public class HaDcClientSelector implements DcClientSelector, DcClientSelectedListener, Closeable {

    private WebClient dcClient;

    private final ExecutorService selectExecutor;

    private InoutProperties properties;

    private SelectTask selectTask;

    public HaDcClientSelector(InoutProperties properties) {

        Assert.isTrue(StringUtils.isNoneBlank(properties.getRemote().getDcIpHosts(), properties.getRemote().getDcDomainHost()), "riemann网关的组VIP或域名缺失");
        this.properties = properties;
        this.selectExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void refresh(InoutProperties properties) {

        Assert.isTrue(StringUtils.isNoneBlank(properties.getRemote().getDcIpHosts(), properties.getRemote().getDcDomainHost()), "riemann网关的组VIP或域名缺失");
        this.properties = properties;
    }

    @Override
    public void start() {

        DcClient[] dcClients = Arrays.stream(properties.getRemote().getDcIpHosts().split(",")).map(s -> new DcClient(s, properties)).toArray(DcClient[]::new);
        this.stop();
        this.selectTask = new SelectTask(dcClients, properties.getRemote().getBaseZno(), this);
        selectExecutor.submit(this.selectTask);
    }

    @Override
    public void stop() {

        if (this.selectTask != null) {
            this.selectTask.stop();
        }
    }

    @Override
    public void close() {

        this.selectExecutor.shutdown();
    }

    @Override
    public WebClient getDcClient() {

        return dcClient;
    }

    @Override
    public void selected(WebClient client) {

        this.dcClient = client;
    }

    private static class DcClient {

        private final String host;

        private WebClient webClient;

        private final String healthCheckUri;

        private final Consumer<HttpHeaders> headersConsumer;

        private boolean enable = true;

        public DcClient(String host, InoutProperties inoutProperties) {

            this.host = host;
            //this.webClient = WebClients.newWebClient(host);
            this.healthCheckUri = inoutProperties.getRemote().getDispGwHealthUri();
            headersConsumer = headers -> {
                headers.add("zno", inoutProperties.getRemote().getBaseZno());
                headers.add("host", inoutProperties.getRemote().getDcDomainHost());
            };
        }

        public Mono<Boolean> healthCheck() {
            return this.webClient
                .get()
                .uri(this.healthCheckUri)
                .headers(headersConsumer)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .flatMap(result -> {
                    log.debug("disp client: {} check success, system time: {}.", this.host, result);
                    this.enable = true;
                    return Mono.just(true);
                })
                .onErrorResume(e -> {
                    log.warn("disp client: {} check failed, change client to disable.", this.host);
                    this.enable = false;
                    return Mono.just(false);
                });
        }

        public WebClient getWebClient() {
            return webClient;
        }

        public boolean isEnable() {
            return enable;
        }
    }

    private static class SelectTask implements Runnable {

        private volatile boolean stop = false;

        private final DcClient selfClient;

        private final DcClient[] dcClients;

        private DcClient currentClient;

        private final DcClientSelectedListener listener;

        public SelectTask(DcClient[] dcClients, String baseZno, DcClientSelectedListener listener) {

            this.dcClients = dcClients;
            this.selfClient = dcClients[Math.abs(baseZno.hashCode()) % this.dcClients.length];
            this.currentClient = selfClient;
            this.listener = listener;
            // 初始化
            this.listener.selected(this.selfClient.getWebClient());
        }

        public void stop() {

            this.stop = true;
        }

        @Override
        public void run() {

            while (!stop) {
                // 回切
                if (this.currentClient != selfClient) {
                    this.selfClient.healthCheck().block();
                    if (this.selfClient.isEnable()) {
                        log.warn("服务端恢复，自动回切: {}.", this.selfClient.host);
                        this.currentClient = this.selfClient;
                        this.listener.selected(this.currentClient.getWebClient());
                    }
                } else {
                    if (this.dcClients.length > 1) {
                        // 健康检查是否可用，不可用时自动漂移
                        this.currentClient.healthCheck().block();
                        if (!this.currentClient.isEnable()) {
                            DcClient change = Arrays.stream(this.dcClients)
                                .filter(c -> c != this.currentClient)
                                .peek(c -> c.healthCheck().block())
                                .filter(DcClient::isEnable)
                                .findFirst()
                                .orElse(null);
                            if (change != null) {
                                log.warn("当前服务端: {}不可用，自动漂移至: {}.", this.currentClient.host, change.host);
                                this.currentClient = change;
                                this.listener.selected(this.currentClient.getWebClient());
                            }
                        }
                    }
                }

                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException e) {
                    this.stop = true;
                }
            }
        }
    }
}
