package cc.emw.mobile.chat.utils;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;

/**
 * Created by sunny.du on 2016/10/21.
 */
public class NativePicUtil {
    public static String getPic(int i){
        List<String> nativePic = new ArrayList();
        nativePic.add("drawable://");
        nativePic.add("drawable://" + R.drawable.group1);
        nativePic.add("drawable://" + R.drawable.group2);
        nativePic.add("drawable://" + R.drawable.group3);
        nativePic.add("drawable://" + R.drawable.group4);
        nativePic.add("drawable://" + R.drawable.group5);
        nativePic.add("drawable://" + R.drawable.group6);
        nativePic.add("drawable://" + R.drawable.group7);
        nativePic.add("drawable://" + R.drawable.group8);
        nativePic.add("drawable://" + R.drawable.group9);
        nativePic.add("drawable://" + R.drawable.group10);
        nativePic.add("drawable://" + R.drawable.group11);
        nativePic.add("drawable://" + R.drawable.group12);
        nativePic.add("drawable://" + R.drawable.group13);
        nativePic.add("drawable://" + R.drawable.group14);
        nativePic.add("drawable://" + R.drawable.group15);

        return nativePic.get(i);
    }
}
