/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dajikala.parser;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import server.properties.ProjectProperties;

/**
 *
 * @author @AmirShk
 */
public class Main {

    public static DijiKalaData getLinkData(String link) throws IOException {
        String baseURL = ProjectProperties.getInstance().getProperty("dijikala.url");
        DijiKalaData dkd = new DijiKalaData();
        dkd.id = link;
        Connection conncetion = Jsoup.connect(baseURL + link);
        Document doc = conncetion.header("Accept-Encoding", "gzip, deflate")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .maxBodySize(0)
                .timeout(600000)
                .get();
        Element name = doc.getElementsByClass("c-product__title").first();
        dkd.persianName = name.html();
        Element img = doc.getElementsByClass("js-gallery-img").first();
        dkd.imageLink = img.attr("data-src");
        Element score = doc.getElementsByClass("u-text-bold js-seller-final-score").first();
        dkd.score = persianToEnglishConverter(score.html());
        Elements isExist = doc.getElementsByClass("c-product__delivery-warehouse js-provider-main-title c-product__delivery-warehouse--no-lead-time");
        if (isExist.size() > 0) {
            dkd.isExist = true;
        } else {
            dkd.isExist = false;
        }
        Element price = doc.getElementsByClass("c-product__seller-price-pure js-price-value").first();
        dkd.price = persianToEnglishConverter(price.html());
        Elements smallImage = doc.getElementsByClass("thumb-wrapper");
        String[] smallImageLinks = new String[smallImage.size()];
        for (int i = 0; i < smallImage.size(); i++) {
            smallImageLinks[i] = smallImage.get(i).select("img").first().attr("data-src");
        }
        dkd.smallImageLinks = smallImageLinks;
        Elements similarGoods = doc.getElementsByClass("c-product-box__box-link js-product-url js-carousel-ga-product-box");
        String[] similarGoodsLinks = new String[similarGoods.size()];
        for (int i = 0; i < similarGoods.size(); i++) {
            similarGoodsLinks[i] = similarGoods.get(i).attr("href");
        }
        dkd.similarGoodsLinks = similarGoodsLinks;
        return dkd;
    }

    public static String persianToEnglishConverter(String num) {
        String res = "";
        for (int i = 0; i < num.length(); i++) {
            String s = Character.toString(num.charAt(i));
            if (s.equals(".")) {
                res += ".";
            } else if (s.equals(",")) {
            } else {
                res += faToEn(s);
            }
        }
        return res;
    }

    public static String faToEn(String num) {
        return num
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
    }
}
