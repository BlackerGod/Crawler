package test;

import crowler.Crawler;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler();
        String html = crawler.getUrl("https://github.com/doov-io/doov");
        System.out.println(html);
    }
}
