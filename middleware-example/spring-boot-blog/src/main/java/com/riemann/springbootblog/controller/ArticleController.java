package com.riemann.springbootblog.controller;

import com.riemann.springbootblog.pojo.Article;
import com.riemann.springbootblog.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 博客列表controller
 */
@Controller
public class ArticleController {

    /**
     * 文章列表服务
     */
    @Autowired
    IArticleService articleService;

    /**
     * 根据分页获取文章列表
     *
     * @param page  当前页
     * @param size  每页条数
     * @param model model类
     * @return 跳转页面
     */
    @RequestMapping("/list")
    public String list(@RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "2") Integer size, Model model) {
        Page<Article> pageList = articleService.list(page, size);
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        return "client/index";
    }
}