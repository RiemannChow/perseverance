package com.riemann.service.impl;

import com.riemann.dao.CodeDao;
import com.riemann.feign.MailFeignClient;
import com.riemann.pojo.AuthCode;
import com.riemann.service.CodeService;
import com.riemann.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    CodeDao codeDao;

    @Autowired
    MailFeignClient mailFeignClient;

    @Override
    public boolean create(String email) {
        AuthCode authCode = new AuthCode();
        authCode.setEmail(email);
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        authCode.setCode(String.valueOf(code));
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime expireTime = createTime.plusMinutes(10);
        authCode.setCreatetime(DateUtil.localDateTime2Date(createTime));
        authCode.setExpiretime(DateUtil.localDateTime2Date(expireTime));
        codeDao.save(authCode);
        mailFeignClient.sendCode(email, String.valueOf(code));
        return true;
    }

    @Override
    public Integer validate(String email, String code) {
        AuthCode authCode = new AuthCode();
        authCode.setEmail(email);
        Example<AuthCode> authCodeExample = Example.of(authCode);
        List<AuthCode> all = codeDao.findAll(authCodeExample);
        // 根据该email查不到验证码记录
        if (null == all || all.size() < 1) {
            return 1;
        }
        authCode.setCode(code);
        authCodeExample = Example.of(authCode);
        Optional<AuthCode> codeOptional = codeDao.findOne(authCodeExample);
        if (!codeOptional.isPresent()) {
            return 1;
        }
        AuthCode lastAuthCode = codeOptional.get();
        // 查到的验证码记录的code为空
        if (lastAuthCode == null || lastAuthCode.getCode() == "" || !lastAuthCode.getCode().equals(code)) {
            return 1;
        }

        // 已过期
        if (new Date().after(lastAuthCode.getExpiretime())) {
            return 2;
        }

        return 0;
    }

}
