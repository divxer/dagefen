package jobs;

import models.Album;
import models.Picture;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.BaseX;
import utils.UpYun;
import utils.UpYunUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: divxer
 * Date: 12-6-4
 * Time: 上午12:17
 */
//@Every("12hr")
@OnApplicationStart
public class MmonlyCrawler extends Job {
    public void doJob() {
        System.out.println("");

        String beautyLegUrl = "http://www.mmonly.com/beauty/";
        try {
            Document doc = Jsoup.connect(beautyLegUrl).timeout(10000).get();

            // 获取文章列表
            Element listPage = doc.select("html body div.wrap div.page ul li a").last();
            System.out.println(listPage.toString());

            // 提取总页数
            Integer totalPages = null;
            String prefix = "";
            String suffix = "";
            String pagePatternStrs = "(.+?list_)(\\d+)(.html)";
            Pattern pagePattern = Pattern.compile(pagePatternStrs, Pattern.DOTALL);
            Matcher pageMatcher = pagePattern.matcher(listPage.attr("abs:href"));
            while (pageMatcher.find()) {
                String pageString = pageMatcher.group(2);
                for (int i = 0; i <=pageMatcher.groupCount(); i++) {
                    System.out.println("i=" + i + "; " + pageMatcher.group(i));
                }
                prefix = pageMatcher.group(1);
                suffix = pageMatcher.group(3);
                totalPages = Integer.parseInt(pageString);
            }
            if (totalPages != null) {
                System.out.println("total pages:" + totalPages);
                for (int i = 1; i <= totalPages; i++) {
                    String pageUrl = prefix + i + suffix;
                    System.out.println(pageUrl);

                    processPage(pageUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processPage(String pageUrl) {
        try {
//                    html body div.wrap div.w650 div.imgList ul li a
            Document doc = Jsoup.connect(pageUrl).timeout(10000).get();
            Elements articleList = doc.select("html body div.wrap div.w650 div.imgList ul li");

            for (Element article : articleList) {
                Element articleLink = article.children().last();
                String articleUrl = articleLink.attr("abs:href");
                String articleTitle = articleLink.text();
                System.out.println("title:" + articleTitle + "; url:" + articleUrl);
                processArticle(articleUrl, articleTitle);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArticle(String articleUrl, String articleTitle) {
        try {
            Document doc = Jsoup.connect(articleUrl).timeout(10000).get();

            Element titleElement = doc.select("html body#body div.wrap div.arcOther div.arcTitle h1 a").first();
            String title = titleElement.text();

            Element descriptionElement = doc.select("html body#body div.wrap div.arcOther div.arcDES").first();
            String description = descriptionElement == null ? "" : descriptionElement.text();

            Album album;
            if (Album.find("byTitle", title).fetch().size() == 0) {
                album = new Album(title, description, "", null);
            } else {
                album = Album.find("byTitle", title).first();
            }

            // 下载图片
            Element imgElement = doc.select("html body#body div.wrap div.arcBody p a img").first();
            String firstImgUrl = imgElement.attr("abs:src");
            downloadImage(album, firstImgUrl, title, description);

            Elements elements = doc.select("html body#body div.wrap div.page ul li");
            elements.remove(0);
            elements.remove(0);
            elements.remove(0);
            elements.remove(elements.size()-1);
            for (Element element : elements) {
                String imgUrl = element.child(0).attr("abs:href");
                getIamges(album, imgUrl, title, description);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getIamges(Album album, String url, String articleTitle, String articleDescription) {
        System.out.println("url:" + url);
        try {
            Document doc = Jsoup.connect(url).timeout(10000).get();

            Element imgElement = doc.select("html body#body div.wrap div.arcBody p a img").first();
            if (imgElement == null) {
                imgElement = doc.select("html body#body div.wrap div.arcBody p img").first();
                if (imgElement == null) {
                    return;
                }
            }
            String imgUrl = imgElement.attr("abs:src");

            // 开始下载图片
            downloadImage(album, imgUrl, articleTitle, articleDescription);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadImage(Album album, String imgUrl, String dirName, String articleDescription) throws IOException {


        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
        HttpGet get = new HttpGet(imgUrl);
        HttpResponse response = httpclient.execute(get);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            System.out.println("error. http status:" + response.getStatusLine().getStatusCode());
            return;
        }

        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);

        File downloadDir = new File(System.getProperty("java.io.tmpdir")
                + "mmonlyPics" + File.separator + dirName + File.separator);
        boolean result = downloadDir.mkdirs();

        String pictureLocation = System.getProperty("java.io.tmpdir")
                + "mmonlyPics" + File.separator  + dirName + File.separator + fileName;

        FileOutputStream output = new FileOutputStream(pictureLocation);
        System.out.println(pictureLocation);

        int chunkSize = 1024 * 8;
        byte[] buf = new byte[chunkSize];
        int readLen;
        while ((readLen = in.read( buf, 0, buf.length)) != -1) {
            output.write(buf, 0, readLen);
        }
        in.close();
        output.close();

        EntityUtils.consume(entity);

        httpclient.getConnectionManager().shutdown();

        File file = new File(pictureLocation);

        // 保存图片
        Picture picture = new Picture(dirName, articleDescription, file.getName(), imgUrl, album);

        // 保存图片专辑
        album.pictures.add(picture);
        Album savedAlbum = album.save();

        String upYunDirName = getTinyUrl(savedAlbum.id.toString());
        String fileUrl = UpYunUtils.picBedDomain + "/" + upYunDirName + "/" + file.getName();

        // 事务提交，并开启新事务
        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();
//        JPA.em().flush();
//        JPA.em().clear();

        // 保存图片到又拍云
        boolean upResult = UpYunUtils.saveImg2Upyun(upYunDirName, pictureLocation);
        // 保存专辑缩略图地址
        if (upResult && album.thumbnail == null) {
            album.thumbnail = fileUrl;
            album.save();
        }
    }

    private static String getTinyUrl(String pictureId) {
        BaseX bx = new BaseX(BaseX.DICTIONARY_32_SMALL);
        String encoded = bx.encode(new BigInteger(pictureId));

        return encoded;
    }
}
