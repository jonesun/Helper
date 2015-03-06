package jone.helper.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jone.helper.bean.News;
import jone.helper.lib.contentProvider.ProcessDataContentProvider;
import jone.helper.lib.processDataOperator.ProcessDBDataOperator;

/***
 * 保存HTMl工具类
 * @author jone.sun
 *
 */
public class DownloadHtmlFrom36krUtil {

    private static DownloadHtmlFrom36krUtil instance;
    public static DownloadHtmlFrom36krUtil getInstance(){
        if(instance == null){
            instance = new DownloadHtmlFrom36krUtil();
        }
        return instance;
    }

    /**
     * 下载网页上图片、JS、CSS
     * @param httpUrl
     * @param urlPath
     */
    @SuppressWarnings("finally")
    private boolean saveHtmlFile(String savePath, String httpUrl, String urlPath) {
        boolean isSuccess = false;
        URL url;
        BufferedInputStream in;
        FileOutputStream file;
        try {
            String fileName = urlPath.substring(urlPath.lastIndexOf("/"));
            String filePath = savePath + urlPath.substring(0, urlPath.lastIndexOf("/"));
            //System.out.println(filePath);
            url = new URL(httpUrl + urlPath);
            File uploadFilePath = new File(filePath);
            if (!uploadFilePath.exists()) {
                uploadFilePath.mkdirs();
            }

            in = new BufferedInputStream(url.openStream());
            file = new FileOutputStream(new File(filePath + fileName));

            int t;
            while ((t = in.read()) != -1) {
                file.write(t);
            }
            file.close();
            in.close();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        }finally{
            return isSuccess;
        }
    }

    public List<String> get36krNews(String htmlContent, String searchReg) throws IOException {
        Pattern pattern = Pattern.compile(searchReg);
        Matcher matcher = pattern.matcher(htmlContent);
        List<String> newsList = new ArrayList<>();
        while (matcher.find()) {
            newsList.add(matcher.group());
            //System.out.println("searchReg: " + matcher.group());
        }
        return newsList;
    }

    public List<News> get36krNews() throws IOException {
        String newsUrl = "http://www.36kr.com/";
        List<News> newses = new ArrayList<>();
        String htmlCode = null;
        htmlCode = getHtmlCode(newsUrl);
        if(htmlCode != null){
            String searchReg1 = "(<div class=\"image feature-img thumb-180\"><a href=\"/p/)(.*?</a></div>)";
            List<String> news_01_list = get36krNews(htmlCode, searchReg1);
            String searchReg2 = "(<h1><a href=\")(.*?</a></h1>)";
            List<String> news_02_list = get36krNews(htmlCode, searchReg2);
            if(news_01_list != null
                    && news_02_list != null
                    && news_01_list.size() > 0
                    && news_01_list.size() == news_02_list.size()){
                for(int i = 0; i < news_01_list.size(); i++){
                    String news_01 = news_01_list.get(i);
                    String news_02 = news_02_list.get(i);
                    String title = news_02.substring(news_02.lastIndexOf("\">") + 2, news_02.lastIndexOf("</a></h1>"));
                    String url = newsUrl + news_02.substring(news_02.indexOf("p/"), news_02.indexOf(".html") + 5);
                    String imageUrl = news_01.substring(news_01.indexOf("http://"), news_01.indexOf("!slider"));
                    newses.add(new News(title, url, imageUrl, "36kr"));
                }
            }
        }
        return newses;
    }

    /**
     * 获得网页html代码
     * @param httpUrl
     * @return
     * @throws IOException
     */
    public String getHtmlCode(String httpUrl) throws IOException {
        String content = "";
        URL uu = new URL(httpUrl); // 创建URL类对象
        BufferedReader ii = new BufferedReader(new InputStreamReader(uu
                .openStream())); // //使用openStream得到一输入流并由此构造一个BufferedReader对象
        String input;
        while ((input = ii.readLine()) != null) { // 建立读取循环，并判断是否有读取值
            content += input;
        }
        ii.close();
        return content;
    }

    public static boolean saveStrToFile(String name, String path) {
        boolean isSuccess = false;
        byte[] b = name.getBytes();
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(path);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    /**
     * 测试方法
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        DownloadHtmlFrom36krUtil gcp = new DownloadHtmlFrom36krUtil();
        List<News> newses = gcp.get36krNews();
//        if(newses != null && newses.size() > 0){
//            for(News news : newses){
//                System.err.println("title: " + news.getTitle()
//                        + ", url: " + news.getUrl()
//                        + ", imageUrl: " + news.getImageUrl()
//                        + ", from: " + news.getFrom());
//            }
//        }
    }
}
