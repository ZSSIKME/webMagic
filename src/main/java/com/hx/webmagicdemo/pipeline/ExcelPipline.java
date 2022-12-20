package com.hx.webmagicdemo.pipeline;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
public class ExcelPipline implements Pipeline {

    private Workbook workbook;

    public ExcelPipline(Workbook workbook) {
        this.workbook = workbook;
        //初始化workBook
        Sheet sheet1 = workbook.createSheet("一级");
        Row sheet1Row = sheet1.createRow(0);
        sheet1Row.createCell(0).setCellValue("BO_STUDY_ARTICLE_ID");
        sheet1Row.createCell(1).setCellValue("ARTICLE_CODE");
        sheet1Row.createCell(2).setCellValue("TITLE");
        sheet1Row.createCell(3).setCellValue("SEQ");

        Sheet sheet2 = workbook.createSheet("二级");
        Row sheet2Row = sheet2.createRow(0);
        sheet2Row.createCell(0).setCellValue("BO_STUDY_YEAR_ARTICLE_ID");
        sheet2Row.createCell(1).setCellValue("BO_STUDY_ARTICLE_ID");
        sheet2Row.createCell(2).setCellValue("ARTICLE_CODE");
        sheet2Row.createCell(3).setCellValue("TITLE");
        sheet2Row.createCell(4).setCellValue("YEAR");
        sheet2Row.createCell(5).setCellValue("SEQ");

        Sheet sheet3 = workbook.createSheet("三级");
        Row sheet3Row = sheet3.createRow(0);
        sheet3Row.createCell(0).setCellValue("BO_STUDY_YEAR_ARTICLE_ITEM_ID");
        sheet3Row.createCell(1).setCellValue("BO_STUDY_YEAR_ARTICLE_ID");
        sheet3Row.createCell(2).setCellValue("ARTICLE_CODE");
        sheet3Row.createCell(3).setCellValue("CONTENT");
        sheet3Row.createCell(4).setCellValue("SOURCE");
        sheet3Row.createCell(5).setCellValue("SEQ");
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        System.out.println("获取到数据url链接: " + resultItems.getRequest().getUrl());
        //处理一级目录数据
        ArrayList<ArrayList<String>> firstMenuList = resultItems.get("firstMenuList");
        if (firstMenuList != null) {
            System.out.println("获取到一级菜单数据大小：" + firstMenuList.size());
            Sheet sheet1 = this.workbook.getSheetAt(0);
            //获取excel的物理行数新增行
            int physicalNumberOfRows = sheet1.getPhysicalNumberOfRows();
            for (int i = 0; i < firstMenuList.size(); i++) {
                Row sheet1Row = sheet1.createRow(physicalNumberOfRows);
                //获取一级目录列表中的数据
                ArrayList<String> arrayList = firstMenuList.get(i);
                sheet1Row.createCell(0).setCellValue(arrayList.get(0));
                sheet1Row.createCell(1).setCellValue(arrayList.get(1));
                sheet1Row.createCell(2).setCellValue(arrayList.get(2));
                sheet1Row.createCell(3).setCellValue(arrayList.get(3));
                physicalNumberOfRows++;
            }
        }
        ArrayList<ArrayList<String>> secondMenuList = resultItems.get("secondMenuList");
        if (secondMenuList != null) {
            System.out.println("获取到二级菜单数据大小：" + secondMenuList.size());
            Sheet sheet2 = this.workbook.getSheetAt(1);
            //获取excel的物理行数新增行
            int physicalNumberOfRows = sheet2.getPhysicalNumberOfRows();
            for (int i = 0; i < secondMenuList.size(); i++) {
                Row sheet2Row = sheet2.createRow(physicalNumberOfRows);
                //获取二级目录菜单的数据
                ArrayList<String> arrayList = secondMenuList.get(i);
                sheet2Row.createCell(0).setCellValue(arrayList.get(0));
                sheet2Row.createCell(1).setCellValue(arrayList.get(1));
                sheet2Row.createCell(2).setCellValue(arrayList.get(2));
                sheet2Row.createCell(3).setCellValue(arrayList.get(3));
                sheet2Row.createCell(4).setCellValue(arrayList.get(4));
                sheet2Row.createCell(5).setCellValue(arrayList.get(5));
                physicalNumberOfRows++;
            }
        }

        ArrayList<ArrayList<String>> thirdMenuList = resultItems.get("thirdMenuList");
        if (thirdMenuList != null) {
            System.out.println("获取到三级菜单数据大小：" + thirdMenuList.size());
            Sheet sheet3 = this.workbook.getSheetAt(2);
            //获取excel的物理行数新增行
            int physicalNumberOfRows = sheet3.getPhysicalNumberOfRows();
            for (int i = 0; i < thirdMenuList.size(); i++) {
                Row sheet3Row = sheet3.createRow(physicalNumberOfRows);
                //获取三级目录菜单数据
                ArrayList<String> arrayList = thirdMenuList.get(i);
                sheet3Row.createCell(0).setCellValue(arrayList.get(0));
                sheet3Row.createCell(1).setCellValue(arrayList.get(1));
                sheet3Row.createCell(2).setCellValue(arrayList.get(2));
                sheet3Row.createCell(3).setCellValue(arrayList.get(3));
                sheet3Row.createCell(4).setCellValue(arrayList.get(4));
                sheet3Row.createCell(5).setCellValue(arrayList.get(5));
                physicalNumberOfRows++;
            }
        }
    }
}
