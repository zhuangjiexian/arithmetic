package cn.linye.sort;

import cn.linye.generalUtils.DataUtils;

public class QuickSort {
    public static void main(String []args){
        int [] array = DataUtils.generalArray;
        querySort(array,0,array.length-1);
        DataUtils.disPlayArray(array);
    }

    private static void querySort(int[] array, int _left, int _right) {
        int left = _left;
        int right = _right;
        int temp=0;

        if(left<=right){
            temp=array[left];
            while (left!=right){
                while (right>left && array[right]>=temp)
                    right--;
                array[left] = array[right];

                while (right>left && array[left]<=temp)
                    left++;
                array[right] = array[left];
            }
            array[right] = temp;
            querySort(array,_left,left-1);
            querySort(array,right+1,_right);
        }
    }
}
