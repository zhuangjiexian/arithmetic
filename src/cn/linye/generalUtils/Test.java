package cn.linye.generalUtils;

public  class Test {
    public static String str="";
    public static  void  main(String [] args){
      tt();
      System.out.println(str);
    }

    public static void tt(){
         try{
             throw new Exception();
         }catch (Exception e){
             str+="t";
             return;
         }finally {
             str+="3";
         }

    }
}

