package com.wx.demo.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static String regexTkl(String regexText) {
        Pattern p = Pattern.compile("\uFFE5(.*?)\uFFE5");
        Matcher m = p.matcher(regexText);
        if (m.find()) {
            return m.group(1);
        }
        Pattern p1 = Pattern.compile("\u300A(.*?)\u300A");
        Matcher m1 = p1.matcher(regexText);
        if (m1.find()) {
            return m1.group(1);
        }
        Pattern p3 = Pattern.compile("\u20AC(.*?)\u20AC");
        Matcher m3 = p3.matcher(regexText);
        if (m3.find()) {
            return m3.group(1);
        }
        Pattern p4 = Pattern.compile("\\$(.*?)\\$");
        Matcher m4= p4.matcher(regexText);
        if (m4.find()) {
            return m4.group(1);
        }
        return null;
    }

    public static String regexEmjoy(String regexText) {
        Pattern p = Pattern.compile("\u8FD9\u6761\u4FE1\u606F(.*?)\u540E");
        Matcher m = p.matcher(regexText);
        if (m.find())
            return m.group(1);
        return null;
    }
    public static String regexSnsId(String regexText) {
        Pattern p = Pattern.compile("<id>(.*?)</id>");
        Matcher m = p.matcher(regexText);
        if (m.find())
            return m.group(1);
        return null;
    }
    public static String regexWxid(String regexText) {
        Pattern p = Pattern.compile("#(.*?)#");
        Matcher m = p.matcher(regexText);
        if (m.find())
            return m.group(1);
        return null;
    }


    public static String regexNumber(String content) {
        Pattern p = Pattern.compile("itemId=(\\d+)?");
        Matcher m = p.matcher(content);
        if (m.find())
            return m.group(1);
        return null;
    }

    public static String regexItemId(String content) {
        Pattern p = Pattern.compile("id=(\\d+)?");
        Matcher m = p.matcher(content);
        if (m.find())
            return m.group(1);
        return null;

    }


    public static String regexItemId1(String content) {
        Pattern p = Pattern.compile("id=(.*?)\\s");
        Matcher m = p.matcher(content + "   ");
        if (m.find())
            return m.group(1);
        return null;
    }

    public static String tmItemId(String content) {
        Pattern p = Pattern.compile("itemId\":(.*?),");
        Matcher m = p.matcher(content + "   ");
        if (m.find())
            return m.group(1);
        return null;
    }

    public static String tmUrl(String content) {
        Pattern p = Pattern.compile("http://yukhj.com(.*?) \\)");
        Matcher m = p.matcher(content + "   ");
        if (m.find())
            return m.group(1);

        Pattern p1 = Pattern.compile("http://yukhj.com(.*?)\\s");
        Matcher m1 = p1.matcher(content + "   ");
        if (m1.find())
            return m1.group(1);
        return null;
    }
    public static String pddItemId(String content) {
        Pattern p = Pattern.compile("goods_id=(.*?)&");
        Matcher m = p.matcher(content + "   ");
        if (m.find())
            return m.group(1);

        Pattern p1 = Pattern.compile("goods_id=(.*?)\\s");
        Matcher m1 = p1.matcher(content + "   ");
        if (m1.find())
            return m1.group(1);
        return null;
    }

    public static String jdItemId(String content) {
        Pattern p = Pattern.compile("jd.com/product/(.*?).html");
        Matcher m = p.matcher(content + "   ");
        if (m.find()){
            return m.group(1);
        }

        Pattern p1 = Pattern.compile("jd.com/(.*?).html");
        Matcher m2 = p1.matcher(content + "   ");
        if (m2.find()){
            return m2.group(1);
        }
        return null;
    }
    private static String stringFilter(String str) {
        String result = "";
        try {
            str = str.replaceAll("\\\\", "");
            String regEx = "[`~!@#$%^&*()+=|{}'.:;'\\[\\]<>/?~@#￥%……&*]";//+号表示空格
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            result = m.replaceAll("");
        } catch (Exception e) {


            return result;
        }

        return result;
    }
}
