package com.hx.webmagicdemo.processer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SpiderProcesser implements PageProcessor {
    private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(100);

    /**
     * 每次使用前先初始化该component缓存
     */
    public void init() {
        this.secondMenuCount = 0;
        this.thirdCount = 1;
        this.secondCount = 1;
        this.firstCount = 1;
        this.firstItemIds = new ArrayList<>();
    }

    private int secondMenuCount = 0;    //二级目录循环次数获取一级目录关联文章的itemId
    private int thirdCount = 1; //三级目录序号
    private int secondCount = 1;    //二级目录序号
    private int firstCount = 1;  //一级目录序号
    private ArrayList<String> firstItemIds = new ArrayList<>();

    @Override
    public void process(Page page) {
        Element body = page.getHtml().getDocument().body();
        /**
         * 分为四个阶段爬虫
         * 第一阶段返回数据可以解析成为json获取pageData.textList 没有channel字段或channel字段为空
         * 第二阶段返回数据可以解析成为json获取pageData.channel  没有textList字段或textList字段为空
         * 第三阶段返回的body数数据为一个数组
         * 第四阶段返回的数据callback开头
         */

        /**
         * 第三阶段需要第一阶段的数据
         */
        //判断是否以‘{’开头，解析成json,为第一阶段或第二阶段
        if (body.text().startsWith("{")) {
            firstMenuHandler(page, body);
        }
        //第三阶段数据返回的是一个数组以'['开头
        if (body.text().startsWith("[")) {
            secondMenuHandler(page, body);
        }

        //第四阶段数据以callback开头js文件
        if (body.text().startsWith("callback")) {
            thirdMenuHandler(page, body);
        }
    }

    /**
     * 一级目录处理
     *
     * @param page
     * @param body
     */
    private void thirdMenuHandler(Page page, Element body) {
        ArrayList<ArrayList> thirdMenuList = new ArrayList<>();

        String text = body.text();
        String substring = body.text().substring(9, text.length() - 1);
        //json转换
        JSONObject forthStr = JSONObject.parseObject(substring);
        //获取文本数据
        String content = forthStr.getString("content");
        //获取itemId
        String itemId = forthStr.getJSONObject("identity").getString("item_id");

        String[] contentList = content.split("\\n\\n");

        String contentStr = "";

        ArrayList<String> contentLists = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));
        contentLists.set(1, itemId);//文章id

        //判断contentList内容是否存在换行符，不存在则根据"VM"和“习近平”字符串进行解析
        if (contentList.length <= 1) {
            String[] vws = content.split("VW");
            for (String vw : vws) {
                if (vw.length() <= 22) {
                    continue;
                }
                vw = "VW" + vw;
                //截取习近平开头
                String[] titles = vw.split("习近平");
                String title = titles[0];
                String artCode = title.substring(0, 22);
                String contentNoStr = title.substring(22);
                String source = "习近平" + titles[1];
                contentLists.set(0, Integer.toString(this.thirdCount));
                contentLists.set(2, artCode);
                contentLists.set(3, contentNoStr);//段落内部详情数据
                contentLists.set(4, source);
                contentLists.set(5, Integer.toString(this.thirdCount));
                //将数据放入item链表中
                ArrayList<String> strings = new ArrayList<>(contentLists);
                thirdMenuList.add(strings);
                this.thirdCount++;
            }
        } else {
            //循环遍历该content
            for (String allKindsStr : contentList) {
                if (!allKindsStr.contains("（本专论在整理更新中）")) {
                    if (allKindsStr.contains("习近平2") || allKindsStr.contains("习近平：")) {
                        contentLists.set(0, Integer.toString(this.thirdCount));
                        contentLists.set(3, contentStr);//段落内部详情数据
                        contentLists.set(4, allKindsStr);
                        contentLists.set(5, Integer.toString(this.thirdCount));
                        //将数据放入item链表中
                        ArrayList<String> strings = new ArrayList<>(contentLists);
                        thirdMenuList.add(strings);
                        contentStr = "";
                        this.thirdCount++;
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
        //每页爬取完成后传递给pipeline
        page.putField("thirdMenuList", thirdMenuList);
    }

    /**
     * 二级目录处理
     *
     * @param page
     * @param body
     */
    private void secondMenuHandler(Page page, Element body) {

        JSONArray listJson = JSON.parseArray(body.text());

        String itemIdFirst = this.firstItemIds.get(this.secondMenuCount);
        ArrayList<ArrayList> secondMenuList = new ArrayList<>();
        for (int i = 0; i < listJson.size(); i++) {
            JSONObject jsonObject = listJson.getJSONObject(i);
            String itemId = jsonObject.getString("itemId");
            String title = jsonObject.getString("title");
            String articleCode = title.substring(0, 9);
            String secondTitle = title.substring(9, title.indexOf("（"));
            String secondYear = title.substring(title.indexOf("（") + 1, title.indexOf("）"));
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(itemId);
            arrayList.add(itemIdFirst);
            arrayList.add(articleCode);
            arrayList.add(secondTitle);
            arrayList.add(secondYear);
            arrayList.add(Integer.toString(this.secondCount++));
            secondMenuList.add(arrayList);
            //访问三级目录
            page.addTargetRequest("https://boot-source.xuexi.cn/data/app/" + itemId + ".js");
        }

        //获取全局变量中的itemId(第一阶段存储的全部变量list）,每一次循环
        page.putField("secondMenuList", secondMenuList);
        this.secondMenuCount++;

    }

    /**
     * 三级目录处理
     *
     * @param page
     * @param body
     */
    private void firstMenuHandler(Page page, Element body) {
        JSONObject pageData = JSONObject.parseObject(body.text()).getJSONObject("pageData");
        //将pageData进行分类
        Iterator<String> iterator = pageData.keySet().iterator();
        ArrayList<String> arrayList = new ArrayList<>();
        while ((iterator.hasNext())) {
            arrayList.add(iterator.next());
        }

        //textList属于第一阶段的数据，第一阶段之访问一次
        if (arrayList.contains("textList")) {
            //判断textList数据不为空
            JSONArray listJson = pageData.getJSONArray("textList");
            ArrayList<Object> firstMenuList = new ArrayList<>();
            //开始执行第一阶段数据的逻辑
            for (int i = 0; i < listJson.size(); i++) {
                JSONObject jsonObject = listJson.getJSONObject(i);
                String itemId = jsonObject.getString("itemId");//获取itemid
                //获取text，
                String text = jsonObject.getJSONObject("title").getString("text");
                String articleCode = text.substring(0, 3);//article_code取前三个字符串
                String tittle = text.substring(3);//contText获取第三个字段之后的所有字段
                ArrayList<String> menuList = new ArrayList<>();
                menuList.add(itemId);
                menuList.add(articleCode);
                menuList.add(tittle);
                menuList.add(Integer.toString(this.firstCount++));
                this.firstItemIds.add(itemId);//将itemId存储到全局变量
                firstMenuList.add(menuList);
                page.addTargetRequest("https://www.xuexi.cn/lgdata/" + itemId + ".json");
            }
            //只爬取一次数据，完成之后直接全部传递给pipeline
            page.putField("firstMenuList", firstMenuList);
        }

        //第二阶段为包含channel值数据
        if (arrayList.contains("channel")) {
            String channelId = pageData.getJSONObject("channel").getString("channelId");
            //添加二级目录获取数据第二阶段url
            page.addTargetRequest("https://www.xuexi.cn/lgdata/" + channelId + ".json");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
