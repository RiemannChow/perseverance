package com.riemann.springbootblog.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "t_article")
public class Article implements Serializable {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章具体内容
     */
    private String content;
    /**
     * 发表时间
     */
    private Date created;
    /**
     * 修改时间
     */
    private Date modified;
    /**
     * 文章分类
     */
    private String categories;
    /**
     * 文章标签
     */
    private String tags;
    /**
     * 是否允许评论
     */
    @Column(name = "allow_comment")
    private Integer allowComment;
    /**
     * 文章缩略图
     */
    private String thumbnail;

}