package cn.linye.sort;

import cn.linye.generalUtils.DataUtils;

public class XuanZe {

    public static void main(String []args){
        int [] array = DataUtils.generalArray;
        int t,k;
        for(int i=0;i<array.length;i++){
            for(int j=i+1;j<array.length;j++){
                if(array[i]>array[j]){
                    t = array[i];
                    array[i] = array[j];
                    array[j] = t;
                }
            }
        }

        DataUtils.disPlayArray(array);
    }
}
