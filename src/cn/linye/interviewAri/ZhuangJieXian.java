package cn.linye.interviewAri;

import java.util.*;

public class ZhuangJieXian{
    public static void main(String []args){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("AA", "111");
        //map.put("BB", "222");
        //map.put("CC", "444");
        map.put("DD","dfd");;
        //.put("EE", "444");
        map.put("FF","dfd");
        map.put("GG","ff");
        //String sql = "select T1.a,T1.b from T1 where 2=2 or ((a = :AA or b=:BB) and (c =:CC or d in :DD ) and e <> :EE) or f like :FF";
        String sql = "select * from T1 where 1=1 and a <= :AA  or ((b =:BB or b =:CC)  or e like :EE)";


        String runnableSql = getRunnableSql(sql,map);
        System.out.println("可运行sql:");
        System.out.println(runnableSql);
    }


    /***
     * 获取可运行sql
     * @param labelSql
     * @param param
     * @return
     */
    public static String getRunnableSql(String labelSql, Map<String, Object> param){
        //第一个左括号
        int leftIndex = labelSql.indexOf("(");
        //最后一个右括号
        int rightIndex =labelSql.lastIndexOf(")");
        StringBuffer runnableSql = new StringBuffer();
        //处理括号的内容得到后缀集合
        List<String> suffix = formatSql(labelSql.substring(leftIndex,rightIndex+1));
        System.out.println("后缀集合：");
        System.out.println(suffix);
        //得到处理后的集合内容
        String KHResult = assignOperation(suffix,param);


        //将原括号内容替换为（#）
        labelSql = labelSql.replace(labelSql.substring(leftIndex,rightIndex+1),"(#)");
        //对元sql根据and进行分割
        List<String> pjList = Arrays.asList(labelSql.split("and"));
        //遍历分割的字符
        for(String sql : pjList) {
            //包含or字符
            if (sql.contains("or")) {
                List<String> orList = Arrays.asList(sql.split("or"));
                for (int i=0;i<orList.size();i++) {
                    String orStr = orList.get(i);
                    runnableSql.append(jointRunnableSql(orStr, param, KHResult, ":", "or",i));
                }
                continue;
            }
            runnableSql.append(jointRunnableSql(sql, param, KHResult, ":", "and",1));
        }
        return  runnableSql.toString();
    }

    public static  StringBuffer jointRunnableSql(String orstr,Map<String, Object> param,String KHResult,String sing,String orAnd,int isFirst){
        StringBuffer runnableSql = new StringBuffer();
            if(orstr.contains(sing)){
                int index = orstr.indexOf(sing);
                String name = orstr.trim().substring(index);
                Object value = param.get(name);
                if(value!=null) {
                    orstr = orstr.replace(":" + name, value.toString());
                    if(isFirst==0){
                        if(orAnd.equals("or")){
                            runnableSql.append(" and ");
                        }else {
                            runnableSql.append(" or ");
                        }
                    }else {
                        runnableSql.append(" "+orAnd+" ");
                    }
                    runnableSql.append(orstr);
                }

            }else if(orstr.trim().equals("(#)")){
                if(!KHResult.trim().equals("#nullValue#")){
                    if(isFirst==0){
                        if(orAnd.equals("or")){
                            runnableSql.append(" and ");
                        }else {
                            runnableSql.append(" or ");
                        }
                    }else {
                        runnableSql.append(" "+orAnd+" ");
                    }
                    runnableSql.append(KHResult);
                }
            }else if(orstr.trim().contains("where")){
                runnableSql.append(orstr);
            }

        return runnableSql;
    }

        /**
         * 格式化sql为类似后缀表达式
         * @param sql
         * @return
         */
    public static List<String> formatSql(String sql){
        Stack<String> stack = new Stack<String>();
        //存储后缀表达式集合
        List<String> suffixList = new ArrayList<String>();
        //存储所有or下标的集合
        List<Integer> orList = getIndex(sql,"or");
        //存储所有and下标的集合
        List<Integer> andList = getIndex(sql,"and");
        //存储所有右括号下标的集合
        List<Integer> rightList = getIndex(sql,")");

        StringBuffer sb = new StringBuffer();
        for(int i=0;i<sql.length();i++){
            //当前小标字符
            char charValue = sql.charAt(i);
            //遇到or字符
            if(orList.size()>0&&orList.get(0)==i){
                orList.remove(0);
                //将拼接的字符放入后缀集合
                if(sb.toString().trim().length()>0){
                    suffixList.add(sb.toString().trim());
                }
                //将or压栈
                stack.push("or");
                sb.delete(0,sb.length());
                i+=1;
                continue;
            //遇到and字符
            }else if(andList.size()>0&&andList.get(0)==i){
                andList.remove(0);
                //将拼接的字符放入后缀集合
                if(sb.toString().trim().length()>0){
                    suffixList.add(sb.toString().trim());
                }
                //将and压栈
                stack.push("and");
                sb.delete(0,sb.length());
                i+=2;
                continue;
            //遇到右括号
            }else if(rightList.size()>0&&rightList.get(0)==i){
                rightList.remove(0);
                if(sb.toString().trim().length()>0){
                    suffixList.add(sb.toString().trim());
                }
                sb.delete(0,sb.length());
                //将栈中的or或and都加入后缀集合
                int len = stack.size();
                for(int j=0;j<len;j++){
                    suffixList.add(stack.pop());
                }
            //遇到普通字符
            }else if((charValue>='a'&&charValue<='z')||(charValue>='A'&&charValue<='Z')){
                sb.append(charValue);
            }else if(charValue=='='||charValue==':'||charValue=='<'||charValue=='>'||charValue==' '){
                sb.append(charValue);
            }

        }
        //返回后缀集合
        return suffixList;
    }


    /**
     * 对sql片段进行赋值拼接
     * @param suffixList
     * @return
     */
    public static  String assignOperation(List<String> suffixList,Map<String, Object> param){
        Stack<String> stack = new Stack<String>();
        //遍历后缀表达式集合
        for(String express : suffixList){
            //如果是and或or则出栈进行处理
            if(express.trim().equals("or")||express.trim().equals("and")){
                String end = stack.pop();
                String begin = stack.pop();
                //对优先拼接的sql片段进行处理
                String result = jointKH(begin,end,express,param);
                //将拼接结果进行压栈
                stack.push(result);
            }else {
                //如果是非and或or则进行压栈
                stack.push(express);
            }
        }
        return  stack.pop();
    }

    /**
     * 对括号内容拼接
     * @param begin
     * @param end
     * @param param
     * @return
     */
    public static String jointKH(String begin,String end ,String andOr,Map<String, Object> param){
        //判断第一段sql是否存在变量标识
        if(begin.contains(":")){
            int index = begin.indexOf(":");
            String name = begin.substring(index+1,begin.length());
            Object value = param.get(name);
            //参数是否有這个变量，有则进行替换
            if(value!=null){
                begin = begin.replace(":"+name,value.toString());
            }else {
                //没有则进行标识
                begin = "#beginNo#";
            }
            //对之前的计算标识进行判断
        }else if(begin.equals("#nullValue#")){
            begin = "#beginNo#";
        }

        //判断第二段sql是否存在变量标识
        if(end.contains(":")){
            int index = end.indexOf(":");
            String name = end.substring(index+1,end.length());
            Object value = param.get(name);
            //参数是否有這个变量，有则进行替换
            if(value!=null){
                end = end.replace(":"+name,value.toString());
            }else {
                //没有则进行标识
                end = "#endNo#";
            }
            //对之前的计算标识进行判断
        }else if(end.equals("#nullValue#")){
            end = "#endNo#";
        }

        //对两段sql的所有情况进行拼接
        StringBuffer sb = new StringBuffer();
        if(begin.equals("#beginNo#")){
            if(end.equals("#endNo#")){
                //第一段sql和第二段sql都删除
                sb.append("#nullValue#");
            }else {
                //删除第一段
                sb.append(end);
            }
        }else {
            if(end.equals("#endNo#")){
                //删除第二段
                sb.append(begin);
            }else {
                //对两段sql进行拼接
                sb.append("(");
                sb.append(begin);
                sb.append(" "+andOr+" ");
                sb.append(end);
                sb.append(")");
            }
        }
        //返回两段sql片段的拼接结果
        return sb.toString();
    }


    /**
     * 获取字符串中所有子字符串的位置
     * @param str 目标字符串
     * @param ch  子字符串
     * @return
     */
    public static List<Integer> getIndex(String str, String ch){
        List<Integer> list = new ArrayList<Integer>();
        int i = 0;
        while (true) {
            int a = str.indexOf(ch, i);
            if (a == -1) {
                return list;
            }
            list.add(a);
            i = a + ch.length();
        }

    }


}
