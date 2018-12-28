package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseHolder;
import cc.emw.mobile.base.MyBaseAdapter;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.view.CircleImageView;

/**
 * 任务列表适配器
 * Created by jven.wu on 2016/6/23.
 */
public class TaskAdapter extends MyBaseAdapter<ApiEntity.UserFenPai> {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private ArrayList<ApiEntity.UserFenPai> tasks = new ArrayList<>(); //任务数据
    private DisplayImageOptions options; //图片显示选项
    private String oldFormat; //原始时间格式
    private String newFormat; //目标时间格式
    private ViewGroup itemViewGroup;

    public TaskAdapter(Context context){
        this.mContext = context;
        //初始化图片显示选项
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        oldFormat = context.getResources().getString(
                R.string.timeformat6);
        newFormat = context.getResources().getString(
                R.string.timeformat7);
        this.mDatas = tasks;
    }

    /**
     * 设置数据
     * @param dataList
     */
    @Override
    public void setData(List<ApiEntity.UserFenPai> dataList) {
        super.setData(dataList);
        if(dataList != null){
            tasks.clear();
            tasks.addAll(dataList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        itemViewGroup = parent;
        ViewHolder vh;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = vh.mHolderView;
        }else {
            vh = (ViewHolder)convertView.getTag();
        }
        ApiEntity.UserFenPai task = tasks.get(position);
        vh.setDataAndRefreshHolderView(position,task);
        Logger.d(TAG,convertView + "");
        return convertView;
    }

    class ViewHolder extends BaseHolder<ApiEntity.UserFenPai>{
        View view;
        TextView taskName;
        TextView taskDate;
        CircleImageView taskImg;

        /**
         * 设置页面数据显示
         * @param postion
         * @param data
         */
        @Override
        public void refreshHolderView(int postion, ApiEntity.UserFenPai data) {
            taskName.setText(data.Title);
            String startTime = TaskUtils.parseToNewStringTime(oldFormat, newFormat, data.StartTime);
            taskDate.setText(startTime);

            if (EMWApplication.personMap != null && EMWApplication.personMap.get(data.Creator) != null) {
                String image = EMWApplication.personMap.get(data.Creator).Image;
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
                ImageLoader.getInstance().displayImage(uri, taskImg, options);
            }
        }

        /**
         * 初始化和绑定页面控件
         * @return
         */
        @Override
        public View initHolderViewAndFindViews() {
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_project_task,itemViewGroup ,false);
            taskName = (TextView)view.findViewById(R.id.task_name);
            taskDate = (TextView)view.findViewById(R.id.task_date);
            taskImg = (CircleImageView)view.findViewById(R.id.task_responser_portrait);
            return view;
        }
    }

}
