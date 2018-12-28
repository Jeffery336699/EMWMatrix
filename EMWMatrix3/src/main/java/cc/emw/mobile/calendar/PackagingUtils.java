package cc.emw.mobile.calendar;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.LinearLayout;

/**
 * Created by ${zrjt} on 2017/1/3.
 */
public class PackagingUtils {

    /**
     * 获取重复自定义周选择某些天
     * @param checkBox1
     * @param checkBox2
     * @param checkBox3
     * @param checkBox4
     * @param checkBox5
     * @param checkBox6
     * @param checkBox7
     */
    public static String getRepeatWeekInfo(CheckBox checkBox1, CheckBox checkBox2, CheckBox checkBox3
            , CheckBox checkBox4, CheckBox checkBox5, CheckBox checkBox6, CheckBox checkBox7) {
        StringBuilder weekNum = new StringBuilder();
        if (checkBox1.isChecked()) {
            weekNum.append("0,");
        }
        if (checkBox2.isChecked()) {
            weekNum.append("1,");
        }
        if (checkBox3.isChecked()) {
            weekNum.append("2,");
        }
        if (checkBox4.isChecked()) {
            weekNum.append("3,");
        }
        if (checkBox5.isChecked()) {
            weekNum.append("4,");
        }
        if (checkBox6.isChecked()) {
            weekNum.append("5,");
        }
        if (checkBox7.isChecked()) {
            weekNum.append("6,");
        }
        if (weekNum.length() > 0) {
            weekNum.deleteCharAt(weekNum.length() - 1);
        }
        return weekNum.toString();
    }

//    public static boolean onClickEvent(View v, CollapseView tiXingCV, CollapseView repeats, CollapseView tiXingEndLayout
//            , TimePickerView mEndRepeatPopupWindow, String endRepeatTime, SwitchButton freeRepeatSw, boolean isFree, LinearLayout freeRepeatSLayout
//            , LinearLayout fixHzRepeatLayout, int fixHeight, CalendarInfo cInfo) {
//        switch (v.getId()) {
//            case cc.emw.mobile.R.id.ll_calendar_tx_none:
//                tiXingCV.setTitle("无");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 0;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_0:
//                tiXingCV.setTitle("准时");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 0;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_1:
//                tiXingCV.setTitle("5分钟前");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 5;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_2:
//                tiXingCV.setTitle("1小时前");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 60;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_3:
//                tiXingCV.setTitle("3小时前");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 180;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_4:
//                tiXingCV.setTitle("1天前");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 1440;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_5:
//                tiXingCV.setTitle("3天前");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 4320;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_6:
//                tiXingCV.setTitle("1周前");
//                tiXingCV.rotateArrow();
//                cInfo.ISCALL = 1;
//                cInfo.AHEAD_MINUTE = 10080;
//                break;
//            case cc.emw.mobile.R.id.ll_never_repeat:
//                repeats.setTitle("无");
//                repeats.rotateArrow();
//                tiXingEndLayout.setVisibility(View.GONE);
//                break;
//            case cc.emw.mobile.R.id.ll_day_repeat:
//                repeats.setTitle("每天");
//                repeats.rotateArrow();
//                cInfo.REPEATTYPE = 1;
//                cInfo.REPEATHZ = 1;
//                tiXingEndLayout.setVisibility(View.VISIBLE);
//                break;
//            case cc.emw.mobile.R.id.ll_week_repeat:
//                repeats.setTitle("每周");
//                repeats.rotateArrow();
//                cInfo.REPEATTYPE = 2;
//                cInfo.REPEATHZ = 1;
//                tiXingEndLayout.setVisibility(View.VISIBLE);
//                break;
//            case cc.emw.mobile.R.id.ll_week_twice_repeat:
//                repeats.setTitle("每两周");
//                repeats.rotateArrow();
//                cInfo.REPEATTYPE = 2;
//                cInfo.REPEATHZ = 2;
//                tiXingEndLayout.setVisibility(View.VISIBLE);
//                break;
//            case cc.emw.mobile.R.id.ll_month_repeat:
//                repeats.setTitle("每月");
//                repeats.rotateArrow();
//                cInfo.REPEATTYPE = 3;
//                tiXingEndLayout.setVisibility(View.VISIBLE);
//                cInfo.REPEATHZ = 1;
//                break;
//            case cc.emw.mobile.R.id.ll_year_repeat:
//                repeats.setTitle("每年");
//                repeats.rotateArrow();
//                cInfo.REPEATTYPE = 4;
//                tiXingEndLayout.setVisibility(View.VISIBLE);
//                cInfo.REPEATHZ = 1;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_none_end:
//                tiXingEndLayout.setTitle("无");
//                tiXingEndLayout.rotateArrow();
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_tx_0_end:
//                tiXingEndLayout.setTitle("");
//                mEndRepeatPopupWindow.show();
//                cInfo.REPEATENDTIME = endRepeatTime;
//                break;
//            case cc.emw.mobile.R.id.ll_calendar_free_switch:
//                freeRepeatSw.toggle();
//                if (freeRepeatSw.isChecked()) {
//                    isFree = true;
//                    repeats.setTitle("自定");
//                    tiXingEndLayout.setVisibility(View.VISIBLE);
//                    freeRepeatSLayout.setVisibility(View.VISIBLE);
//                    fixHeight = fixHzRepeatLayout.getMeasuredHeight();
//                    hideAnimation(fixHzRepeatLayout);
//                } else {
//                    isFree = false;
//                    repeats.setTitle("无");
//                    tiXingEndLayout.setVisibility(View.GONE);
//                    freeRepeatSLayout.setVisibility(View.GONE);
//                    fixHzRepeatLayout.setVisibility(View.VISIBLE);
//                    showAnimation(fixHzRepeatLayout, fixHeight);
//                }
//                break;
//        }
//        return isFree;
//    }

    public static void showAnimation(final LinearLayout fixHzRepeatLayout, final int fixHeight) {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Log.d("zrjt", fixHeight + "");
                if (interpolatedTime == 1) {
                    fixHzRepeatLayout.getLayoutParams().height = fixHeight;
                } else {
                    fixHzRepeatLayout.getLayoutParams().height = (int) (fixHeight * interpolatedTime);
                    fixHzRepeatLayout.setVisibility(View.VISIBLE);
                }
                fixHzRepeatLayout.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(1000);
        fixHzRepeatLayout.startAnimation(animation);
    }

    public static void hideAnimation(final LinearLayout fixHzRepeatLayout) {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    fixHzRepeatLayout.setVisibility(View.GONE);
                } else {
                    fixHzRepeatLayout.getLayoutParams().height = fixHzRepeatLayout.getMeasuredHeight() -
                            (int) (fixHzRepeatLayout.getMeasuredHeight() * interpolatedTime);
                    fixHzRepeatLayout.requestLayout();
                }
            }
        };
        animation.setDuration(1000);
        fixHzRepeatLayout.startAnimation(animation);

    }
}
