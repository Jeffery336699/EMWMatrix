package cc.emw.mobile.entity;

import java.io.Serializable;
import java.util.ArrayList;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEntity.UserSchedule;

public class UserNote extends ApiEntity.UserNote {
	private static final long serialVersionUID = 1L;

	//ADD
	public NoteInfo info;
	//END
	
	public UserNote() {
		DevType = 2;
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
		public UserNoteLink link;
		public UserNoteShareTo shareTo; // 转发
		public ArrayList<UserNoteFile> File; // 图片或文件
		public ArrayList<UserRootVote> vote; // 投票
		public ArrayList<UserFenPai> task;
		public ArrayList<UserPlan> log; // 工作计划
		public ArrayList<UserSchedule> schedule; // 日程
//		public NoteRecord record; // 记录
	}

	public static class UserRootVote implements Serializable {
		public ArrayList<UserNoteVote> Content;
		public int Type;
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
	}
	// 分享投票
	public static class UserNoteVote implements Serializable {
		private static final long serialVersionUID = 1L;

		public String Text;
		public int ID;
		public int Count;
		public boolean IsSelected;
	}

	// 分享链接
	public static class UserNoteLink implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String addr; // 链接地址
		public String desc; // 链接描述
	}
}
