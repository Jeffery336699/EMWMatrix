package cc.emw.mobile.task.entity;

import java.io.Serializable;
import java.util.List;

/**
 * task实体
 * @author chengyong.liu
 *
 */
public class UserFenPai implements Serializable{
	 /**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	public int ID;//任务ID
     public int ProjectId;//工作项目
     public int State;//任务状态：1未开始，2进行中，3已完成
     public String Title;//任务标题
     public String StartTime;//任务开始时间
     public String FinishTime;//任务完成时间
     public String CreateTime;//任务创建时间
     public int Creator;//任务创建者ID
     public int Color;//任务颜色
     public String MainUser;//主要负责人ID    1,23,4...
     public String MoreUser;//更多负责人ID 字符串   1,23,4....
     public int PID;//任务父ID
     public int Tier;
     public int Depends; //关联任务ID
     public List<UserFenPai> Tasks;//子任务
     public String Mark;//任务描述
     public String Files;//附件文件[1,23,9];
     public String Progress;//进度值
     public String Line_Schedule;//相关日程[101,2,100]
     public int FlowState;//任务权限  1普通2提交审核3返工4通过审核
     public int Yxj;//任务优先级 1代表普通 2代表紧急 3代表非常紧急
	public List<UserFenPai> getTasks() {
		return Tasks;
	}
	public void setTasks(List<UserFenPai> tasks) {
		Tasks = tasks;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getProjectId() {
		return ProjectId;
	}
	public void setProjectId(int projectId) {
		ProjectId = projectId;
	}
	public int getState() {
		return State;
	}
	public void setState(int state) {
		State = state;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getFinishTime() {
		return FinishTime;
	}
	public void setFinishTime(String finishTime) {
		FinishTime = finishTime;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public int getCreator() {
		return Creator;
	}
	public void setCreator(int creator) {
		Creator = creator;
	}
	public int getColor() {
		return Color;
	}
	public void setColor(int color) {
		Color = color;
	}
	public String getMainUser() {
		return MainUser;
	}
	public void setMainUser(String mainUser) {
		MainUser = mainUser;
	}
	public String getMoreUser() {
		return MoreUser;
	}
	public void setMoreUser(String moreUser) {
		MoreUser = moreUser;
	}
	public int getPID() {
		return PID;
	}
	public void setPID(int pID) {
		PID = pID;
	}
	public int getTier() {
		return Tier;
	}
	public void setTier(int tier) {
		Tier = tier;
	}
	public int getDepends() {
		return Depends;
	}
	public void setDepends(int depends) {
		Depends = depends;
	}
	public String getMark() {
		return Mark;
	}
	public void setMark(String mark) {
		Mark = mark;
	}
	public String getFiles() {
		return Files;
	}
	public void setFiles(String files) {
		Files = files;
	}
	public String getProgress() {
		return Progress;
	}
	public void setProgress(String progress) {
		Progress = progress;
	}
	public String getLine_Schedule() {
		return Line_Schedule;
	}
	public void setLine_Schedule(String line_Schedule) {
		Line_Schedule = line_Schedule;
	}
	public int getFlowState() {
		return FlowState;
	}
	public void setFlowState(int flowState) {
		FlowState = flowState;
	}
	public int getYxj() {
		return Yxj;
	}
	public void setYxj(int yxj) {
		Yxj = yxj;
	}
	
     
}
