package cc.emw.mobile.login;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.decorators.EventDecorator;
import com.prolificinteractive.materialcalendarview.decorators.TodayDecorator;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.map.ToastUtil;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.util.DisplayUtil;

@ContentView(R.layout.activity_test)
public class TestActivity extends BaseActivity {

	private Handler mHandler = new Handler();
    WebView mWebView;

    @ViewInject(R.id.calendarView)
    private MaterialCalendarView widget;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);

        widget.setLeftArrowMask(getResources().getDrawable(R.drawable.btn_calendar_month_leftarrow));
        widget.setRightArrowMask(getResources().getDrawable(R.drawable.btn_calendar_month_rightarrow));
        widget.setTitleFormatter(new DateFormatTitleFormatter()); //设置顶部日期格式
        widget.setTileWidth(DisplayUtil.getDisplayWidth(this) / 7); //设置单元格宽度
        widget.setCurrentDate(CalendarDay.from(Calendar.getInstance())); //跳转到指定日期
        widget.addDecorator(new TodayDecorator(this)); //当前日期空心圆显示
        widget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Calendar calendar = date.getCalendar();
                ToastUtil.show(TestActivity.this, calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            }
        });
        widget.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                Calendar calendar = date.getCalendar();
                widget.setSelectedDate(calendar);
            }
        });

        List<CalendarDay> calendarDays = new ArrayList<>();
        for (int i = 0; i< 100; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            calendarDays.add(CalendarDay.from(calendar));
        }
        //添加事件，显示小圆点
        widget.addDecorator(new EventDecorator(getResources().getColor(R.color.mcv_event_normal), calendarDays));

        ((Button)findViewById(R.id.btn_test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*widget.setSelectedDate(Calendar.getInstance());
                widget.setCurrentDate(CalendarDay.from(Calendar.getInstance()), true);*/
                if (v.getTag() != null && Integer.valueOf(v.getTag().toString()) == 0) {
                    widget.setCalendarDisplayMode(CalendarMode.WEEKS);
                    widget.setTopbarVisible(false);
                    v.setTag(1);
                } else {
                    widget.setCalendarDisplayMode(CalendarMode.MONTHS);
                    widget.setTopbarVisible(true);
                    v.setTag(0);
                }
            }
        });

        /*mWebView = new WebView(this);
        setContentView(mWebView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.setWebViewClient(new WebViewClient());
//        mWebView.setWebViewClient(new MyWebViewClient());//让WebView支持弹出框
        *//*
mWebView.addJavascriptInterface(new Object() {
            public void clickOnAndroid() {
                mHandler.post(new Runnable() {
                    public void run() {
                        mWebView.loadUrl("javascript:wave()");
                    }
                });
            }
        }, "demo");*//*
        mWebView.loadUrl("file:///android_asset/demos/index.html");
    }

    // 如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身，
    // 如果希望浏览的网 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    // 内部类
    public class MyWebViewClient extends WebViewClient {
        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
        // 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
        public boolean shouldOverviewUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            showProgress();
        }

        public void onPageFinished(WebView view, String url) {
//            closeProgress();
        }

        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
//            closeProgress();
        }*/
    }
}
