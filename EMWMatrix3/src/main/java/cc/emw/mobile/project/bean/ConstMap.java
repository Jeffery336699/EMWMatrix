package cc.emw.mobile.project.bean;

import java.util.HashMap;
import java.util.Map;

import cc.emw.mobile.R;

/**
 * Created by jven.wu on 2016/7/20.
 */
public class ConstMap {
    public static int getProjectColor(int colorIndex){
        Map<Integer,Integer> projectColors = new HashMap<>();
        projectColors.put(0, R.drawable.project_color5);
        projectColors.put(ConstEnum.ProjectColor.LightBlue, R.drawable.project_color1);
        projectColors.put(ConstEnum.ProjectColor.Orange, R.drawable.project_color2);
        projectColors.put(ConstEnum.ProjectColor.Red, R.drawable.project_color3);
        projectColors.put(ConstEnum.ProjectColor.DarkBlue, R.drawable.project_color4);
        return projectColors.get(colorIndex);
    }

    public static String getColorTxt(int colorIndex){
        Map<Integer,String> projectColorTxts = new HashMap<>();
        projectColorTxts.put(0, "无");
        projectColorTxts.put(ConstEnum.ProjectColor.LightBlue, "浅蓝");
        projectColorTxts.put(ConstEnum.ProjectColor.Orange, "橙色");
        projectColorTxts.put(ConstEnum.ProjectColor.Red, "红色");
        projectColorTxts.put(ConstEnum.ProjectColor.DarkBlue, "深蓝");
        return projectColorTxts.get(colorIndex);
    }
}