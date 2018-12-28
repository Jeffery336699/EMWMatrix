package cc.emw.mobile.entity;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.githang.androidcrash.util.AppManager;
import com.volokh.danylo.visibility_utils.items.ListItem;

import java.io.Serializable;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEntity.UserSchedule;
import cc.emw.mobile.record.jcvideoplayer_lib.JCMediaManager;
import cc.emw.mobile.record.jcvideoplayer_lib.JCUtils;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayerStandard;

public class UserNote extends ApiEntity.UserNote implements ListItem {
	private static final long serialVersionUID = 1L;

	//ADD
	public NoteInfo info;
	//END
	
	public UserNote() {
		DevType = 2;
	}

	private final transient Rect mCurrentViewRect = new Rect();
	@Override
	public int getVisibilityPercents(View currentView) {
//		if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);

		int percents = 100;

		currentView.getLocalVisibleRect(mCurrentViewRect);
//		if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents mCurrentViewRect top " + mCurrentViewRect.top + ", left " + mCurrentViewRect.left + ", bottom " + mCurrentViewRect.bottom + ", right " + mCurrentViewRect.right);

		int height = currentView.getHeight();
//		if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);

		if(viewIsPartiallyHiddenTop()){
			// view is partially hidden behind the top edge
			percents = (height - mCurrentViewRect.top) * 100 / height;
		} else if(viewIsPartiallyHiddenBottom(height)){
			percents = mCurrentViewRect.bottom * 100 / height;
		}

//		setVisibilityPercentsText(currentView, percents);
//		if(SHOW_LOGS) Logger.v(TAG, "<< getVisibilityPercents, percents " + percents);

		return percents;
	}

	/*private void setVisibilityPercentsText(View currentView, int percents) {
//		if(SHOW_LOGS) Logger.v(TAG, "setVisibilityPercentsText percents " + percents);
		VideoViewHolder videoViewHolder = (VideoViewHolder) currentView.getTag();
		String percentsText = "Visibility percents: " + String.valueOf(percents);

		videoViewHolder.mVisibilityPercents.setText(percentsText);
	}*/

	private boolean viewIsPartiallyHiddenBottom(int height) {
		return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
	}

	private boolean viewIsPartiallyHiddenTop() {
		return mCurrentViewRect.top > 0;
	}

	//监听listItem 查找到视频，自动播放视频
	@Override
	public void setActive(View newActiveView, int newActiveViewPosition) {
		Log.e("UserNote", "setActive() >> position:"+newActiveViewPosition);
		JCMediaManager.instance().releaseMediaPlayer();
		if (AppManager.currentActivity() != null && JCUtils.isWifiConnected(AppManager.currentActivity())) {
			LinearLayout videoLayout = (LinearLayout) newActiveView.findViewById(R.id.ll_dynamic_video);
			if (videoLayout != null && videoLayout.getChildCount() > 0) {
				if (videoLayout.getChildAt(0) instanceof JCVideoPlayerStandard) {
					//视频播放功能,取消自动播放功能
					//((JCVideoPlayerStandard) videoLayout.getChildAt(0)).startVideo();
				}
			}
		}
	}

	@Override
	public void deactivate(View currentView, int position) {

	}
	
	/*public int AddType; // UserNoteAddTypes   // 0普通；2图片；3文件；4链接；5投票
	public int SendType; // UserNoteSendTypes // 0为公开，1为私有
	public int MessageCount; // 点赞
	public int EnjoyCount; // 点赞数
	public int Type; // 0普通；1公告；6记录；7日程；8任务；9计划
	public int DevType = 2; // 1:PC; 2:Android; 3:iOS
	public List<NoteRole> Roles; // 可见的角色
	public int ToUserId;*/
	
	public static class NoteInfo implements Serializable {
		public UserNoteLink link; //链接
		public ArrayList<UserNoteShareTo> shareTo; //转发(发布者、内容)
		public UserNote shareNote; //转发(全部信息)
		public ArrayList<UserNoteFile> File; //图片或文件
		public ArrayList<UserRootVote> vote; //投票
		public ArrayList<UserFenPai> task; //工作分派
		public ArrayList<UserPlan> log; //工作计划
		public ArrayList<UserSchedule> schedule; //日程
		public BusDataInfo busData; //业务数据
//		public UserNoteRecord record; //记录
	}

	public static class UserRootVote implements Serializable {
		public ArrayList<UserNoteVote> Content;
		public int Type;
		public String EndTime;
		public int Count;
	}

	// 转发
	public static class UserNoteShareTo implements Serializable {
		public int NoteID; // 
		public String UserName; // 
		public String Content;
	}

	// 分享文件
	public static class UserNoteFile implements Serializable {
		private static final long serialVersionUID = 1L;

		public int FileId;
		public String FileName = ""; // 文件名称
		public String Url;
		public long Length; // 长度
		public int CreateUser; // 发布人名称
		public String CompanyCode;
	}
	// 分享投票
	public static class UserNoteVote implements Serializable {
		private static final long serialVersionUID = 1L;

		public String Text;
		public int ID;
		public int Count;
		public String Url;
		public String Tag;
		public int TP;
		public String CompanyCode;
		public boolean IsSelected;
	}

	// 分享链接
	public static class UserNoteLink implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String addr; // 链接地址
		public String desc; // 链接描述
		public String title;
	}
}
