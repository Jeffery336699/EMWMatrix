/**
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.emw.mobile.project.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 某项目中的任务Adapter
 */
public class StateBoardAdapter extends DragItemAdapter<ApiEntity.UserFenPai, StateBoardAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private DisplayImageOptions options;

    public StateBoardAdapter(ArrayList<ApiEntity.UserFenPai> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setHasStableIds(true);
        setItemList(list);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ApiEntity.UserFenPai task = mItemList.get(position);
        holder.mProjectNameTv.setVisibility(View.GONE);
        if (task.State == TaskConstant.TaskState.FINISHED) {
            CommonUtil.addStrikeSpan(task.Title, holder.mTaskNameTv);
            holder.mTaskNameTv.setTextColor(holder.mGrabView.getContext().getResources().getColor(R.color.gray_3));
            holder.mTaskStateCb.setChecked(true);
        } else {
            holder.mTaskNameTv.setText(task.Title);
            holder.mTaskNameTv.setTextColor(holder.mGrabView.getContext().getResources().getColor(R.color.cm_text));
            holder.mTaskStateCb.setChecked(false);
        }
        //根据任务优先级设置背景色
        holder.mTagLayout.setBackgroundResource(CommonUtil.getTaskBg(task.Yxj));
        holder.mTagLayout.setPadding(0, 0, 0, DisplayUtil.dip2px(holder.mGrabView.getContext(), 10));

        CommonUtil.setTaskDeadLineTimeString(holder.mTaskTimeTv, task);

        if (EMWApplication.personMap != null && EMWApplication.personMap.get(task.Creator) != null) {
            String image = EMWApplication.personMap.get(task.Creator).Image;
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(holder.mUserHeadIv), options, new ImageSize(DisplayUtil.dip2px(holder.mGrabView.getContext(), 40), DisplayUtil.dip2px(holder.mGrabView.getContext(), 40)), null, null);
        }

        holder.itemView.setTag(task);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).ID;
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        public TextView mTaskNameTv;
        public TextView mProjectNameTv;
        public TextView mTaskTimeTv;
        public CircleImageView mUserHeadIv;
        public RelativeLayout mTagLayout;
        public CheckBox mTaskStateCb;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mTaskNameTv = (TextView) itemView.findViewById(R.id.sbc1_task_name);
            mProjectNameTv = (TextView) itemView.findViewById(R.id.sbc1_project_name);
            mTaskTimeTv = (TextView) itemView.findViewById(R.id.sbc1_time);
            mUserHeadIv = (CircleImageView) itemView.findViewById(R.id.scb1_portrait);
            mTagLayout = (RelativeLayout) itemView.findViewById(R.id.sbc1_container_ll);
            mTaskStateCb = (CheckBox) itemView.findViewById(R.id.sbc1_cb);
        }

        @Override
        public void onItemClicked(View view) {
            Intent taskDetailIntent = new Intent(mGrabView.getContext(), TaskDetailActivity.class);
            taskDetailIntent.putExtra(TaskDetailActivity.TASK_DETAIL, (ApiEntity.UserFenPai)view.getTag());
            taskDetailIntent.putExtra("start_anim", false);
            mGrabView.getContext().startActivity(taskDetailIntent);
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}
