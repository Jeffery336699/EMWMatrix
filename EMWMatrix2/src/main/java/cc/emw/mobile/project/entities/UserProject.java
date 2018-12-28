package cc.emw.mobile.project.entities;

import java.io.Serializable;
import java.util.List;
import cc.emw.mobile.task.entity.UserFenPai;

public class UserProject implements Serializable{
	public int ID;
	public String Name;
	public int Creator;
	public String CreateTime;
	public String Mark;
	public String KeyInfo;
	public String BeginTime;
	public String EndTime;
	public String MainUser;
	public String MainUserName;
	public int Color;
	private List<UserFenPai> Tasks;
	private String Progress;
	private String Line_Schedule;
	private String Line_File;
	
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getCreator() {
		return Creator;
	}
	public void setCreator(int creator) {
		Creator = creator;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getMark() {
		return Mark;
	}
	public void setMark(String mark) {
		Mark = mark;
	}
	public String getKeyInfo() {
		return KeyInfo;
	}
	public void setKeyInfo(String keyInfo) {
		KeyInfo = keyInfo;
	}
	public String getBeginTime() {
		return BeginTime;
	}
	public void setBeginTime(String beginTime) {
		BeginTime = beginTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	public String getMainUser() {
		return MainUser;
	}
	public void setMainUser(String mainUser) {
		MainUser = mainUser;
	}
	public String getMainUserName() {
		return MainUserName;
	}
	public void setMainUserName(String mainUserName) {
		MainUserName = mainUserName;
	}
	public int getColor() {
		return Color;
	}
	public void setColor(int color) {
		Color = color;
	}
	public List<UserFenPai> getTasks() {
		return Tasks;
	}
	public void setTasks(List<UserFenPai> tasks) {
		Tasks = tasks;
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
	public String getLine_File() {
		return Line_File;
	}
	public void setLine_File(String line_File) {
		Line_File = line_File;
	}
	
}
