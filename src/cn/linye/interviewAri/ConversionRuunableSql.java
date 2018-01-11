package cn.linye.interviewAri;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionRuunableSql {
    public static void main(String []args){
        String labelSql= "select T1.a,T1.b from T1 where 2=2 and ((a = :AA or b=:BB) and (c =:CC or d in :DD )) and f like :FF";
        Map<String, Object> map = new HashMap<String, Object>();
        //map.put("AA", "111");
        map.put("BB", "222");
        //map.put("CC", "444");
        //map.put("DD","dfd");;
        //.put("EE", "444");
        map.put("FF","dfd");
        map.put("GG","ff");
        String runnableSql = getRunnableSql(labelSql,map);
        System.out.println(runnableSql);


    }

    public static String getRunnableSql(String labelSql, Map<String,Object> param){
        String regex1 = "(or|and)?\\s?[\\(?\\w?\\s?]\\s?(=|like|in)?\\s?:\\w+";

        String regex2 = "(?<=:)\\w+";

        Pattern pattern1 = Pattern.compile(regex1);

        Pattern pattern2 = Pattern.compile(regex2);

        Matcher matcher = pattern1.matcher(labelSql);

        // 匹配找出以下
        // and a = :aa
        // b=:bb
        // or b =:cc
        // and e like :ee
        while (matcher.find()) {
            // 用于aa bb这些
            String paramXX = null;
            // 提取到要替换的字符串
            String paramReplace = matcher.group();
            System.out.println(paramReplace);

            Matcher matcher2 = pattern2.matcher(paramReplace);
            // 再找出:aa中的aa
            if (matcher2.find()) {
                paramXX = matcher2.group();

            }
            // 找出map中的参数
            Object mapParam = param.get(paramXX);

            // 判断是否为null，为null则删除
            if (mapParam == null) {
                labelSql = labelSql.replaceAll(paramReplace, " ");
            } else {
                // 替换
                labelSql = labelSql.replaceAll(":" + paramXX, mapParam.toString());
            }

        }
        labelSql = labelSql.replaceAll("(and|or)\\s?\\(\\s*+\\)\\s", "");
        //select T1.a,T1.b from T1 where 2=2 and ((  or b=222) and (  or d in dfd )) and f like dfd
        labelSql = labelSql.replaceAll("(?<=\\()\\s+(or|and)\\s?","");

        //System.out.println(labelSql);

        return labelSql;

    }


}
