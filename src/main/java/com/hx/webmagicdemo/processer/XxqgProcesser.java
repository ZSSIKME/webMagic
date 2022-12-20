package com.hx.webmagicdemo.processer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.Arrays;

public class XxqgProcesser implements PageProcessor {

    /**
     * 初始化PageProcessor的内容
     */
    void clear(){
        this.articleList = null;
        this.yearArticleList = null;
        this.yearArtItemList = null;
        this.firstCount = 0;
        this.currentFirstCount = 1;
        this.currentThirdCount = 1;
        this.menuLevel = "1";
    }
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    ArrayList<ArrayList<String>> articleList = new ArrayList<>();//一级目录
    ArrayList<ArrayList<String>> yearArticleList = new ArrayList<>();//二级目录
    ArrayList<ArrayList<String>> yearArtItemList = new ArrayList<>();//三级目录

    private int firstCount = 0;
    private int currentFirstCount = 1;
    private int currentThirdCount = 1;
    private String menuLevel = "1";

    @Override
    public void process(Page page) {

        if ("1".equals(menuLevel)) {
            firstStage(page);
        } else if ("2".equals(this.menuLevel)) {
            secondStage(page);
        } else if ("3".equals(this.menuLevel)) {
            thirdStage(page);
        } else if ("4".equals(this.menuLevel)) {
            forthStage(page);
        } else {
            //不进行处理
        }

        //爬取完成之后将数据返回给pipeline
        page.putField("articleList", this.articleList);
        page.putField("yearArticleList", this.yearArticleList);
        page.putField("yearArtItemList", this.yearArtItemList);
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    public void firstStage(Page page) {
        System.out.println("===========第一阶段============");
        //一级目录
        Html html = page.getHtml();
        Document document = html.getDocument();
        Element body = document.body();
        //处理一级目录
        JSONArray listJson = JSONObject.parseObject(body.text()).getJSONObject("pageData").getJSONArray("textList");

        this.firstCount = listJson.size();
        //循环遍历jsonarray 获取爬取地址爬取二级目录
        if (listJson != null && listJson.size() > 0) {
            for (int i = 0; i < listJson.size(); i++) {
                JSONObject jsonObject = listJson.getJSONObject(i);
                String itemId = jsonObject.getString("itemId");//获取itemid
                //获取text，
                String text = jsonObject.getJSONObject("title").getString("text");
                String articleCode = text.substring(0, 3);//article_code取前三个字符串
                String tittle = text.substring(3);//contText获取第三个字段之后的所有字段
                //获取到字段后将一级目录所需字段存储在arrayList中传递给pipeline
                int firstOrder = this.articleList.size();
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(itemId);
                arrayList.add(articleCode);
                arrayList.add(tittle);
                arrayList.add(Integer.toString(++firstOrder));
                this.articleList.add(arrayList);//一级目录存储数组中
                //访问二级目录
                page.addTargetRequest("https://www.xuexi.cn/lgdata/" + itemId + ".json");
                System.out.println("====itemId:" + itemId + "=====articleCode:" + articleCode + "======tittle:" + tittle);
            }
            //一级目录遍历完成之后将目录变成2，单线程启动
            this.menuLevel = "2";
        }
    }

    public void secondStage(Page page) {
        System.out.println("===========第二阶段============");
        //二级目录第一阶段解析
        Html html = page.getHtml();
        Element body = html.getDocument().body();
        String channelId = JSONObject.parseObject(body.text()).getJSONObject("pageData")
                .getJSONObject("channel").getString("channelId");
        System.out.println("channelId=======" + channelId);
        //添加二级目录获取数据第二阶段url
        page.addTargetRequest("https://www.xuexi.cn/lgdata/" + channelId + ".json");
        if (this.currentFirstCount < this.firstCount) {
            this.menuLevel = "2";
            this.currentFirstCount++;
        } else {
            this.menuLevel = "3";
        }
    }

    public void thirdStage(Page page) {
        System.out.println("========第三阶段===========");
        Element body = page.getHtml().getDocument().body();
        //处理二级mul
        JSONArray listJson = JSON.parseArray(body.text());
        for (int i = 0; i < listJson.size(); i++) {
            JSONObject jsonObject = listJson.getJSONObject(i);
            String itemId = jsonObject.getString("itemId");
            String title = jsonObject.getString("title");
            String articleCode = title.substring(0, 9);
            //获取articleList中的外键itemId
            String itemIdFor = this.articleList.get(currentThirdCount - 1).get(0);
            int thirdOrder = this.yearArticleList.size();//序号
            ArrayList<String> arrayList = new ArrayList<>();

            arrayList.add(itemId);
            arrayList.add(itemIdFor);
            arrayList.add(articleCode);
            arrayList.add(title.substring(9, title.indexOf("（")));
            arrayList.add(title.substring(title.indexOf("（") + 1, title.indexOf("）")));
            arrayList.add(Integer.toString(++thirdOrder));
            this.yearArticleList.add(arrayList);
            System.out.println("itemId=====" + itemId);
            //访问三级目录
            page.addTargetRequest("https://boot-source.xuexi.cn/data/app/" + itemId + ".js");
        }
        if (this.currentThirdCount < this.firstCount) {
            this.menuLevel = "3";
            this.currentThirdCount++;
        } else {
            this.menuLevel = "4";
        }
    }

    public void forthStage(Page page) {
        //第四阶段将标识符设为true，将数据传递给pipeline
        System.out.println("================第四阶段===============");
        Element body = page.getHtml().getDocument().body();
        String text = body.text();
        String substring = text.substring(9, text.length() - 1);
        //json转换
        JSONObject forthStr = JSONObject.parseObject(substring);
        //获取文本数据
        String content = forthStr.getString("content");
        //获取itemid
        String itemId = forthStr.getJSONObject("identity").getString("item_id");

        String[] contentList = content.split("\\n\\n");

        String contentStr = "";

        ArrayList<String> contentLists = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));
        //序号
        contentLists.set(0, Integer.toString(this.yearArtItemList.size() + 1));
        contentLists.set(1, itemId);//文章id
        contentLists.set(5, Integer.toString(this.yearArtItemList.size() + 1));
        //循环遍历该content
        for (String allKindsStr : contentList) {

            if (!allKindsStr.contains("（本专论在整理更新中）")) {
                if (allKindsStr.contains("习近平2") || allKindsStr.contains("习近平：")) {

                    contentLists.set(3, contentStr);//段落内部详情数据
                    contentLists.set(4, allKindsStr);
                    //将数据放入item链表中
                    ArrayList<String> strings = new ArrayList<>(contentLists);
                    this.yearArtItemList.add(strings);
                    contentStr ="";
                } else {
                    if (allKindsStr.contains("VW")) {
                        contentLists.set(2, allKindsStr);
                    } else {
                        //可能有多个段落的数据
                        contentStr = contentStr + allKindsStr;
                    }
                }
            }
        }
    }
}
