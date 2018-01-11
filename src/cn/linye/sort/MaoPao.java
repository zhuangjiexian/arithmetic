package cn.linye.sort;

import cn.linye.generalUtils.DataUtils;

public class MaoPao {
    public  static void main(String []args){
        int []array = DataUtils.generalArray;
        int t;
        for(int i=0;i<array.length-1;i++){
            for(int j=0;j<array.length-i-1;j++){
                if(array[j]>array[j+1]){
                    t = array[j+1];
                    array[j+1] = array[j];
                    array[j] = t;
                }
            }
        }

        DataUtils.disPlayArray(array);
    }
}
