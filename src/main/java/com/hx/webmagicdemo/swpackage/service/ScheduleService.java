package com.hx.webmagicdemo.swpackage.service;

import com.hx.webmagicdemo.studymodel.StudyArticle;
import com.hx.webmagicdemo.studymodel.StudyYearArticle;
import com.hx.webmagicdemo.studymodel.StudyYearArticleItem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
@Service
public class ScheduleService {
    @Autowired
    private StudyArticleService articleService;

    public void parseExcelInsert() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\350.xlsx"));
        try {
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            //获取第一个sheet内容
            Sheet sheetAt = workbook.getSheetAt(0);
            ArrayList<StudyArticle> studyArticles = parseFirstExcel(sheetAt);

            //解析二级目录
            Sheet secondMenu = workbook.getSheetAt(1);
            ArrayList<StudyYearArticle> studyYearArticles = parseSecondExcel(secondMenu);

            //解析三级目录
            Sheet thirdMenu = workbook.getSheetAt(2);
            ArrayList<StudyYearArticleItem> studyYearArticleItems = parseThirdExcel(thirdMenu);
            articleService.insertItem(studyYearArticleItems);
            workbook.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            fileInputStream.close();
        }

    }


    public ArrayList<StudyArticle> parseFirstExcel(Sheet sheetAt){
        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
        ArrayList<StudyArticle> studyArticles = new ArrayList<>();
        //从第二行（下标为1）开始读取数据
        for (int i = 1; i < physicalNumberOfRows; i++) {
            Row row = sheetAt.getRow(i);
            if(row!=null){
                StudyArticle studyArticle = new StudyArticle();
                String articleId = row.getCell(0).getStringCellValue();
                String articleCode = row.getCell(1).getStringCellValue();
                String title = row.getCell(2).getStringCellValue();
                Integer seq = Integer.parseInt(row.getCell(3).getStringCellValue());

                //设置值，并添加到列表中
                studyArticle.setBoStudyArticleId(articleId);
                studyArticle.setArticleCode(articleCode);
                studyArticle.setTitle(title);
                studyArticle.setSeq(seq);
                studyArticles.add(studyArticle);
            }
        }

        return studyArticles;
    }

    public ArrayList<StudyYearArticle> parseSecondExcel(Sheet secondMenu){
        int secondMenuRows = secondMenu.getPhysicalNumberOfRows();
        ArrayList<StudyYearArticle> studyYearArticles = new ArrayList<>();
        for (int i = 1; i <= secondMenuRows; i++) {
            Row row = secondMenu.getRow(i);
            if(row!=null){
                StudyYearArticle studyYearArticle = new StudyYearArticle();
                String yearArticleId = row.getCell(0).getStringCellValue();
                String articleId = row.getCell(1).getStringCellValue();
                String articleCode = row.getCell(2).getStringCellValue();
                String title = row.getCell(3).getStringCellValue();
                String year = row.getCell(4).getStringCellValue();
                Integer seq = Integer.parseInt(row.getCell(5).getStringCellValue());

                studyYearArticle.setBoStudyYearArticleId(yearArticleId);
                studyYearArticle.setBoStudyArticleId(articleId);
                studyYearArticle.setArticleCode(articleCode);
                studyYearArticle.setTitle(title);
                studyYearArticle.setYear(year);
                studyYearArticle.setSeq(seq);
                studyYearArticles.add(studyYearArticle);
            }
        }
        return studyYearArticles;
    }

    public ArrayList<StudyYearArticleItem> parseThirdExcel(Sheet thirdMenu){
        int thirdMenuRows = thirdMenu.getPhysicalNumberOfRows();
        ArrayList<StudyYearArticleItem> studyYearArticleItems = new ArrayList<>();
        for (int i = 1; i <= thirdMenuRows; i++) {
            Row row = thirdMenu.getRow(i);
            if(row!=null){
                StudyYearArticleItem studyYearArticleItem = new StudyYearArticleItem();
                String articleItemId = row.getCell(0).getStringCellValue();
                String yearArticleId = row.getCell(1).getStringCellValue();
                String articleCode = row.getCell(2).getStringCellValue();
                String content = row.getCell(3).getStringCellValue();
                String source = row.getCell(4).getStringCellValue();
                Integer seq = Integer.parseInt(row.getCell(5).getStringCellValue());

                studyYearArticleItem.setBoStudyYearArticleItemId(articleItemId);
                studyYearArticleItem.setBoStudyYearArticleId(yearArticleId);
                studyYearArticleItem.setArticleCode(articleCode);
                studyYearArticleItem.setContent(content);
                studyYearArticleItem.setSource(source);
                studyYearArticleItem.setSeq(seq);

                studyYearArticleItems.add(studyYearArticleItem);
            }
        }
        return studyYearArticleItems;
    }
}
