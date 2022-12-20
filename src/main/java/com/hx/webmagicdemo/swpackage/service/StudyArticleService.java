package com.hx.webmagicdemo.swpackage.service;

import com.hx.webmagicdemo.studymodel.StudyYearArticleItem;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudyArticleService {

    public void insertItem(ArrayList<StudyYearArticleItem> studyYearArticleItems){
        //批量插入
        List<List<StudyYearArticleItem>> partition = ListUtils.partition(studyYearArticleItems, 5000);
        for (int i = 0; i < partition.size(); i++) {
            List<StudyYearArticleItem> batchSaveList = partition.get(i);
            insert(batchSaveList);
        }
    }


    //插入方法
    void insert(List<StudyYearArticleItem> studyYearArticleItems){
        System.out.println(studyYearArticleItems);
    }
}
