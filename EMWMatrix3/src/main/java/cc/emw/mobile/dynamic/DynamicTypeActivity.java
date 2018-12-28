package cc.emw.mobile.dynamic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.dynamic.adapter.SpinerAdapter;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * 动态详情
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_dynamic_type)
public class DynamicTypeActivity extends BaseActivity implements SpinerAdapter.IOnItemSelectListener{

    private final static String TAG = "DynamicTypeActivity";
    @ViewInject(R.id.root)
    private View root;//整个界面
    @ViewInject(R.id.listview)
    private ListView mListView;//显示内容列表
  //  @ViewInject(R.id.spinner_time_choose)
  //  private Spinner spinner;//下拉菜单
    @ViewInject(R.id.tv_time_choose)
    private TextView time_choose_tv;
    @ViewInject(R.id.talker_time_start_choose)
    private TextView start_tv;//开始时间选择
    @ViewInject(R.id.talker_time_end_choose)
    private TextView end_tv;//结束时间选择
    @ViewInject(R.id.talker_time_start_choose)
    private TextView start_time_tv;
    @ViewInject(R.id.talker_time_end_choose)
    private TextView end_time_tv;

    private ArrayList<ListDialog.Item> mItemList;
    private DialogAdapter mAdapter;
    private int dynamicType;

    private List<String> spinnerStr;
    private SpinerAdapter spAdater;
    private SpinerPopWindow spinerPopWindow;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(DynamicTypeActivity.this,
                    R.string.endingtime_notlessthan_starttime,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEnableGesture(false);
        dynamicType = getIntent().getIntExtra("type_id", 0);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //下拉菜单内容
        spinnerStr = new ArrayList<>();
        spinnerStr.add("全    部");
        spinnerStr.add("一天内");
        spinnerStr.add("三天内");
        spinnerStr.add("一周内");
        spinnerStr.add("自定义");
      //  adapter = new ArrayAdapter<String>(DynamicTypeActivity.this,android.R.layout.simple_spinner_item,spinnerStr);
        //adapter.add(spinnerStr);
       // adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
       // spinner.setAdapter(adapter);
       // spinner.setDropDownVerticalOffset(16);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        //初始化自定义的PopuWindow时间选择
        spAdater = new SpinerAdapter(DynamicTypeActivity.this,spinnerStr);
        spAdater.refreshData(spinnerStr,0);
        spinerPopWindow = new SpinerPopWindow(DynamicTypeActivity.this);
        spinerPopWindow.setAdatper(spAdater);
        spinerPopWindow.setItemListener(this);

        time_choose_tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // getCompoundDrawables获取是一个数组，数组0,1,2,3,对应着左，上，右，下 这4个位置的图片，如果没有就为null
                Drawable drawable = time_choose_tv.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null){
                    return false;
                }
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
//                if (event.getX() > time_choose_tv.getWidth()
//                        - time_choose_tv.getPaddingRight()
//                        - drawable.getIntrinsicWidth()){
                    showSpinWindow();
//                }
                return false;
            }
        });

        mItemList = new ArrayList<>();
        mItemList.add(new ListDialog.Item(getString(R.string.dynamic_more_all), ApiEnum.UserNoteAddTypes.Normal));
        mItemList.add(new ListDialog.Item("动态", ApiEnum.UserNoteAddTypes.Action));
        mItemList.add(new ListDialog.Item("图片", ApiEnum.UserNoteAddTypes.Image));
        mItemList.add(new ListDialog.Item("视频", ApiEnum.UserNoteAddTypes.Video));
        mItemList.add(new ListDialog.Item("文件", ApiEnum.UserNoteAddTypes.File));
        mItemList.add(new ListDialog.Item("链接", ApiEnum.UserNoteAddTypes.Link));
        mItemList.add(new ListDialog.Item("投票", ApiEnum.UserNoteAddTypes.Vote));
        //个人用户 没有以下筛选
        if (!"PUB".equalsIgnoreCase(PrefsUtil.readUserInfo().CompanyCode)) {
            mItemList.add(new ListDialog.Item(getString(R.string.dynamic_more_schedule), ApiEnum.UserNoteAddTypes.Schedule));
            mItemList.add(new ListDialog.Item(getString(R.string.dynamic_more_allot), ApiEnum.UserNoteAddTypes.Task));
            mItemList.add(new ListDialog.Item(getString(R.string.dynamic_more_plan), ApiEnum.UserNoteAddTypes.Plan));
            mItemList.add(new ListDialog.Item("约会", ApiEnum.UserNoteAddTypes.Appoint));
            mItemList.add(new ListDialog.Item("活动", ApiEnum.UserNoteAddTypes.SeviceActive));
            mItemList.add(new ListDialog.Item("邮件", ApiEnum.UserNoteAddTypes.Email));
            mItemList.add(new ListDialog.Item("电话", ApiEnum.UserNoteAddTypes.Phone));
        }
        mItemList.add(new ListDialog.Item("转发", ApiEnum.UserNoteAddTypes.Share));
        mAdapter = new DialogAdapter(this, mItemList);
        mAdapter.setShowIcon(true);
        mAdapter.setShowTypeIcon(true);
        mAdapter.setShowImgType(false);
        for (int i = 0, count = mItemList.size(); i < count; i++) {
            if (mItemList.get(i).id == dynamicType) {
                mAdapter.setSelectItem(i);
                break;
            }
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                intent.putExtra("type_id", mAdapter.getItem(position).id);
                sendBroadcast(intent);
                finish();
            }
        });

        start_time_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                initShowDateDialog(start_time_tv);
            }
        });

        end_time_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                initShowDateDialog(end_time_tv);
            }
        });
        //筛选框半透明
        this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {

    }

    //显示自定义SpinnerWindow
    private void showSpinWindow(){
        //设置mSpinerPopWindow显示的宽度
        spinerPopWindow.setWidth(time_choose_tv.getWidth());
        //设置显示的位置在哪个控件的下方
        spinerPopWindow.showAsDropDown(time_choose_tv);
    }

    // 将字符串转为时间戳
    private long getLongTime(String user_time) {
        long re_time = 0;
        String pattern = "yyyy-MM-dd";
        if (user_time.length() > 10) {
            pattern = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date d;
        try {
            d = sdf.parse(user_time);
            re_time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }
    //显示选择时间对话框
    public void initShowDateDialog(final TextView textView){
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
               String mStrStartTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                switch (textView.getId()){
                    case R.id.talker_time_start_choose:
                        if(isErrorStartEndTime(mStrStartTime,end_time_tv)){
                              textView.setText(mStrStartTime);
                              textView.setTag(mStrStartTime);
                        }
                        break;
                    case R.id.talker_time_end_choose:
                        if(isErrorStartEndTime(start_time_tv,mStrStartTime)){
                            textView.setText(mStrStartTime);
                            textView.setTag(mStrStartTime);
                        }
                        break;
                }
            }
        });
        pickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        pickerDialog.setAccentColor(getResources().getColor(R.color.cm_main_text));
        pickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }
    //判断两个时间点 时间差
    public boolean isErrorStartEndTime(Object oneObject,Object secondOject){
        String oneTag ,twoTag;
        if(oneObject instanceof String){
           oneTag = (String) oneObject;
        }else {
            TextView oneTv = (TextView) oneObject;
            oneTag= (String) oneTv.getTag();
        }
        if(secondOject instanceof String){
            twoTag = (String) secondOject;
        }else {
            TextView twoTv = (TextView) secondOject;
            twoTag= (String) twoTv.getTag();
        }
        if(oneTag != null && !TextUtils.isEmpty(oneTag)&& twoTag != null && !TextUtils.isEmpty(twoTag)){
            long oneTagLong  = getLongTime(oneTag);
            long twoTagLong =  getLongTime(twoTag);
            if (oneTagLong > twoTagLong){
                mHandler.sendEmptyMessage(0);
                return false;
            }
        }
        return true;
    }
    //检查数值，1-9前面添加一个0，例如：01,02，数值在两位以上就默认两位
    private String checkNum(int num) {
        if (num >= 10) {
            return num + "";
        } else {
            return "0" + num;
        }
    }
    @Override
    public void onItemClick(int pos) {
        String value = spinnerStr.get(pos);
        time_choose_tv.setText(value);
    }

    public static class DialogAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<ListDialog.Item> mDataList;
        private int selectItem;
        private boolean isShow; //是否右边出现选择的√
        private boolean isShowType; //是否左边显示类型图标，动态筛选类型专用
        private boolean isShowImgType; //新建圈子协作，颜色选择专用

        public DialogAdapter(Context context,
                             ArrayList<ListDialog.Item> dataList) {
            this.mContext = context;
            this.mDataList = dataList;
        }

        public void setSelectItem(int position) {
            selectItem = position;
        }

        public void setShowIcon(boolean isShow) {
            this.isShow = isShow;
        }

        public void setShowTypeIcon(boolean isShow) {
            this.isShowType = isShow;
        }
        public void setShowImgType(boolean isShow) {
            this.isShowImgType = isShow;
        }

        @Override
        public int getCount() {
            return mDataList != null ? mDataList.size() : 0;
        }

        @Override
        public ListDialog.Item getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_dynamic_type, null);
                vh.typeItv = (IconTextView) convertView.findViewById(R.id.itv_dialoglist_typeicon);
                vh.typeIv = (ImageView) convertView.findViewById(R.id.iv_dialoglist_typeicon);
                vh.textTv = (TextView) convertView.findViewById(R.id.tv_dialoglist_type);
                vh.iconTextView = (IconTextView) convertView.findViewById(R.id.itv_dialoglist_icon);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            ListDialog.Item item = mDataList.get(position);
            vh.textTv.setText(item.text);
            if (isShow) {
                vh.iconTextView.setVisibility(selectItem == position? View.VISIBLE : View.GONE);
            }
            if (isShowType) {
//                HelpUtil.setDynamicType(position, vh.typeItv);
                HelpUtil.setDynamicType(position, vh.typeIv);
            }
            if (isShowImgType) {
                vh.typeIv.setImageResource(CommonUtil.getProjectRoundColor(item.id));
                vh.typeIv.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        IconTextView typeItv;
        ImageView typeIv;
        TextView textTv;
        IconTextView iconTextView;
    }
}
