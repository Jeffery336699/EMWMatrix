package cc.emw.mobile.file.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.asr.AsrTxtActivity;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.file.FileListActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * 知识库Fragment
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.fragment_file)
public class FileFragment extends BaseFragment {

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderMoreBtn; // 顶部条右菜单按钮

    @ViewInject(R.id.ll_file_root)
    private LinearLayout mRootLayout;
    private float lastY = 0;
    private String headerTitle;
    private int page = 1; // 第几页，第1页为1，每下一页+1
    private static final int PAGE_COUNT = 10; // 页数

    private GestureDetector mGestureDetector;
    private FloatingActionButton mTvAddChat;

    public static FileFragment newInstance(String title) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putString("header_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderMenuBtn.setIconText("ec7c");
        mHeaderMenuBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("知识库");
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderMoreBtn.setIconText("ecd5");
        mHeaderMoreBtn.setVisibility(View.VISIBLE);

        view.findViewById(R.id.rl1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileListActivity.class);
                intent.putExtra("file_type", 3);
                intent.putExtra("file_name", getString(R.string.file_myfile));
                startActivity(intent);
            }
        });
        view.findViewById(R.id.rl2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileListActivity.class);
                intent.putExtra("file_type", 4);
                intent.putExtra("file_name", getString(R.string.file_sharefile));
                startActivity(intent);
            }
        });
        view.findViewById(R.id.rl3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileListActivity.class);
                intent.putExtra("file_type", 6);
                intent.putExtra("file_name", getString(R.string.file_mysharefile));
                startActivity(intent);
            }
        });
        view.findViewById(R.id.rl4).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileListActivity.class);
                intent.putExtra("file_type", 5);
                intent.putExtra("file_name", getString(R.string.file_cancelfile));
                startActivity(intent);
            }
        });

        mGestureDetector = new GestureDetector(getActivity(), new MyOnGestureListener());
        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);

                return true;
            }
        });

        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware((CircleImageView)findView(R.id.cm_header_civ_head)), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 40), DisplayUtil.dip2px(getActivity(), 40)), null, null);

        mTvAddChat = (FloatingActionButton) view.findViewById(R.id.ic_tv_add_chat);
        mTvAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(getActivity(), AsrTxtActivity.class);
                startActivity(scanIntent);
            }
        });
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // if (Math.abs(e1.getRawX() - e2.getRawX()) > 250) {
            // // System.out.println("水平方向移动距离过大");
            // return true;
            // }
            if (Math.abs(velocityY) < 100) {
                // System.out.println("手指移动的太慢了");
                return true;
            }

            // 手势向下 down
            if ((e2.getRawY() - e1.getRawY()) > ViewConfiguration.getTouchSlop()) {
                toggleTopAndBottom("cc.emw.mobile.show_top_and_bottom");
                return true;
            }
            // 手势向上 up
            if ((e1.getRawY() - e2.getRawY()) > ViewConfiguration.getTouchSlop()) {
                toggleTopAndBottom("cc.emw.mobile.hide_top_and_bottom");
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public void toggleTopAndBottom(String action) {
        Intent intentBroadCast = new Intent();
        intentBroadCast.setAction(action);
        getActivity().sendBroadcast(intentBroadCast);
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.cm_header_civ_head, R.id.cm_header_btn_notice})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
            case R.id.cm_header_civ_head:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
            case R.id.cm_header_btn_notice:
//			Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
//			getActivity().startActivity(noticeIntent);
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
        }
    }


}
