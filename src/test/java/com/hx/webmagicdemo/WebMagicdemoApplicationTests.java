package com.hx.webmagicdemo;

import com.hx.webmagicdemo.fileutils.FTPFileUtil;
import com.hx.webmagicdemo.pipeline.ExcelPipline;
import com.hx.webmagicdemo.processer.SpiderProcesser;
import com.hx.webmagicdemo.studymodel.StudyArticle;
import com.hx.webmagicdemo.studymodel.StudyYearArticle;
import com.hx.webmagicdemo.studymodel.StudyYearArticleItem;
import com.hx.webmagicdemo.swpackage.service.ScheduleService;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
class WebMagicdemoApplicationTests {

}
