package com.hx.webmagicdemo;

import com.hx.webmagicdemo.fileutils.FTPFileUtil;
import com.hx.webmagicdemo.pipeline.ExcelPipline;
import com.hx.webmagicdemo.processer.SpiderProcesser;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.io.*;
import java.time.LocalDateTime;

@Service
public class SpiderSchedul {

    @Value("${spiders.rootUrl}")
    private String rootUrl;

    @Value("${spiders.fileLocalPath}")
    private String fileLocalPath;

    @Value("${upLoadFiles.isUpLoad}")
    private Boolean isUpload;

    @Value("${upLoadFiles.hostName}")
    private String hostName;

    @Value("${upLoadFiles.port}")
    private int port;

    @Value("${upLoadFiles.user}")
    private String user;

    @Value("${upLoadFiles.password}")
    private String password;

    @Value("${upLoadFiles.filePath}")
    private String filePath;


    //每天晚上22点全量爬取数据
//    @Scheduled(cron = "0 0 22 * * ? ")
    @Scheduled(cron = "0 0/5 * * * ? ")
    void spider() {
        //创建好workBook，并传递具体的url即可
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SpiderProcesser spiderProcesser = new SpiderProcesser();
        spiderProcesser.init();
        Spider.create(spiderProcesser)
                .addUrl(rootUrl)
                .addPipeline(new ExcelPipline(workbook))
                .run();

        //此时workbook以准备完毕，开始保存
        LocalDateTime now = LocalDateTime.now().minusDays(1);//todo 服务器测试需要减一天
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        int dayOfMonth = now.getDayOfMonth();

        //获取前一天的日期
        LocalDateTime lastDay = now.minusDays(1);
        String lastDayFileName = lastDay.getYear() + "-" + lastDay.getMonthValue() + "-" + lastDay.getDayOfMonth();
        String fileName = year + "-" + monthValue + "-" + dayOfMonth;
        File file = new File(fileLocalPath + fileName + ".xlsx");
        try (OutputStream ops = new FileOutputStream(file)) {
            workbook.write(ops);
            //这里关闭Workbook或者关闭OutputStream都可以，应该是Workbook关闭的时候顺带关闭了OutputStream
            ops.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将数据写入完成之后上传给外部服务器
        if (isUpload) {
            System.out.println("上传外部服务器。。。。"+isUpload);
            try {
                //上传文件
                FTPFileUtil.uploadFileFromProduction(hostName, port, user, password, filePath, fileLocalPath + fileName + ".xlsx");
                //删除前一天的数据
                System.out.println("开始删除前一天的文件。。。。");
//                FTPFileUtil.deleteFile(hostName, port, user, password, filePath, lastDayFileName + ".xlsx");
            } catch (Exception e) {
                System.out.println("文件上传外部服务器失败。。。。。");
                e.printStackTrace();
            }
        }
    }
}
