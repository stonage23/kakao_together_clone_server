package com.kakao.together.api.htmlparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsoupHtmlParser {

    public static List<RawTag> parseBodyFragment(String body) {
        List<RawTag> tags = new ArrayList<>();
        Document doc = Jsoup.parseBodyFragment(body);
        Elements elements = doc.body().children();

        for (Element el : elements) {
            Map<String, String> attrMap = el.attributes().asList().stream()
                    .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue));

            tags.add(new RawTag(el.tagName(), attrMap, el.text()));
        }

        return tags;
    }

    public static List<RawTag> extractTagsFromBody(String body, String tagName) {
        List<RawTag> tags = new ArrayList<>();
        Document doc = Jsoup.parseBodyFragment(body);
        Elements elements = doc.body().children();

        for (Element el : elements) {

            if (!el.tagName().equals(tagName)) continue;

            Map<String, String> attrMap = el.attributes().asList().stream()
                    .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue));

            tags.add(new RawTag(el.tagName(), attrMap, el.text()));
        }

        return tags;
    }
}
