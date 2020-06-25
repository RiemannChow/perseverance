package com.riemann.springbootblog.dao;

import com.riemann.springbootblog.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文章列表dao
 */
public interface ArticleDao extends JpaRepository<Article, Integer> {
}