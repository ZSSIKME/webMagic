package com.hx.webmagicdemo.studymodel;

import lombok.Data;

import java.util.Date;

@Data
public class StudyYearArticleItem {
    /**
     * 序列化变量
     */
    private static final long serialVersionUID = -85395856010616471L;

    /**
     * 主键id
     */
    private String boStudyYearArticleItemId;
    /**
     * 年度学习文汇id
     */
    private String boStudyYearArticleId;
    /**
     * 文汇编号
     */
    private String articleCode;
    /**
     * 内容
     */
    private String content;
    /**
     * 来源
     */
    private String source;
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
