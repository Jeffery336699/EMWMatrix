package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * 团队成员列表适配器
 * Created by jven.wu on 2016/6/23.
 */
public class TeamMemberAdapter2 extends BaseAdapter {
    public static final int TYPE_GRID = 0; //用于GridView列表适配器
    public static final int TYPE_LIST = 1; //用于ListView列表适配器

    private Context mContext;
    private int type = TYPE_GRID;
    private ArrayList<ApiEntity.UserInfo> members = new ArrayList<>(); //成员id集合
    private DisplayImageOptions options; //图片显示选项
    private OnDelClickListener delClickListener;
    private int CreatorId = -1; //

    public TeamMemberAdapter2(Context context, int type){
        this.mContext = context;
        this.type = type;
    }

    public void setData(ArrayList<ApiEntity.UserInfo> members){
        this.members.clear();
        this.members.addAll(members);

        //初始化图片显示选项
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setCreatorId(int id){
        this.CreatorId = id;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView == null){
            vh = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            //根据type选择要展示的列表样式
            if(type == TYPE_GRID) {
                convertView = inflater.inflate(
                        R.layout.listitem_team_member_grid, parent, false);
            }else if(type == TYPE_LIST){
                convertView = inflater.inflate(
                        R.layout.listitem_team_member_list, parent, false);
            }
            vh.teamMemberPortrait =
                    (CircleImageView)convertView.findViewById(R.id.team_member_portrait) ;
            vh.teamMemberName = (TextView) convertView.findViewById(R.id.team_member_name);
            vh.delIcon = (IconTextView)convertView.findViewById(R.id.del_icon);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }

        final ApiEntity.UserInfo user = members.get(position);
        String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
        if (user.Image != null && user.Image.startsWith("/")) {
            uri = Const.DOWN_ICON_URL2 + user.Image;
        }
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.teamMemberPortrait), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);

        vh.teamMemberName.setText(user.Name);

        if(CreatorId > 0 && PrefsUtil.readUserInfo().ID == CreatorId){
            vh.delIcon.setVisibility(View.VISIBLE);
            vh.delIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(delClickListener != null){
                        delClickListener.delClick(position);
                    }
                }
            });
        }
        vh.teamMemberPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(user.ID) != null) {
                    Intent intent = new Intent(mContext, PersonInfoActivity.class);
                    intent.putExtra("UserInfo",EMWApplication.personMap.get(user.ID));
                    intent.putExtra("intoTag",1);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    class ViewHolder{
        CircleImageView teamMemberPortrait; //团队成员头像
        TextView teamMemberName; //团队成员名称
        IconTextView delIcon;  //删除按钮
    }

    public void setOnDelClickListener(OnDelClickListener listener){
        this.delClickListener = listener;
    }

    public interface OnDelClickListener{
        void delClick(int position);
    }
}
