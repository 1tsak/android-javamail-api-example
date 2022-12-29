package com.a2v10.javamailapiexample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLUtils {

    public static String extractPlainTextFromHtml(String html) {
        StringBuilder plainText = new StringBuilder();

        // Parse the HTML document
        Document doc = Jsoup.parse(html);

        // Extract the text from all the elements
        Elements elements = doc.body().select("*");
        for (Element element : elements) {
            // Append the text of the element to the plain text string
            plainText.append(element.text());
        }

        return plainText.toString();
    }

}


