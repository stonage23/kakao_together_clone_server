package com.kakao.together.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlParsingUtil {

    public static Elements buildElementsFromBody(String html) {
        if (html == null) return new Elements();
        Document doc = Jsoup.parseBodyFragment(html);
        return doc.body().children();
    }
}
