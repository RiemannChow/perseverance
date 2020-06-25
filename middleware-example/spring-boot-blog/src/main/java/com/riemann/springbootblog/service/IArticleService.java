package com.riemann.springbootblog.service;

import com.riemann.springbootblog.pojo.Article;
import org.springframework.data.domain.Page;

/**
 * 文章列表服务接口
 */
public interface IArticleService {
    /**
     * 分页查询
     *
     * @param page 当前页
     * @param size 每页条数
     * @return
     */
    Page<Article> list(Integer page, Integer size);
}