package com.hx.webmagicdemo.studymodel;

import lombok.Data;

import java.util.Date;

@Data
public class StudyYearArticle {

    /**
     * 序列化变量
     */
    private static final long serialVersionUID = -42819723706146642L;

    /**
     * 主键id
     */
    private String boStudyYearArticleId;
    /**
     * 学习文汇主表id
     */
    private String boStudyArticleId;
    /**
     * 文汇编号
     */
    private String articleCode;
    /**
     * 标题
     */
    private String title;
    /**
     * 文汇年度
     */
    private String year;
    /**
     * 序号
     */
    private Integer seq;
    /**
     * 创建人
     */
    private String createUserid;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 最后修改人
     */
    private String updateUserid;
    /**
     * 最后修改时间
     */
    private Date updateDate;
}
