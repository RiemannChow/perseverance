package com.riemann.springbootblog.service.impl;

import com.riemann.springbootblog.dao.ArticleDao;
import com.riemann.springbootblog.pojo.Article;
import com.riemann.springbootblog.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private ArticleDao articleDao;

    /**
     * 分页查询
     *
     * @param page 当前页
     * @param size 每页条数
     * @return
     */
    public Page<Article> list(Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1 , size, sort);
        Page<Article> articleList = articleDao.findAll(pageable);
        return articleList;
    }
}