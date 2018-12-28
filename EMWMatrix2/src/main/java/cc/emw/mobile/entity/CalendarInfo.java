package cc.emw.mobile.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserSchedule;

import android.annotation.SuppressLint;

/**
 * @author zrjt
 */
public class CalendarInfo extends UserSchedule implements
		Comparable<CalendarInfo>, Serializable {

	public CalendarInfo(int iD, int userID, int state, String title,
						String startTime, String overTime, int type, int priority,
						int allday, int color, String remark, String notePriority,
						String line_Task, String line_Project, String line_File,
						String line_Group, int aHEAD_MINUTE, int iSCALL, int rEPEATTYPE,
						String rEPEATENDTIME, int rEPEATHZ, String rEPEATWEEKVAL,
						int noteId, int sType, int isJobSync, String repeatTimeVal,
						int repeatMonthDayVal, String repeatYearDayVal,
						List<NoteRole> noteRoles, String noteContent) {
		super();
		ID = iD;
		UserID = userID;
		State = state;
		Title = title;
		StartTime = startTime;
		OverTime = overTime;
		Type = type;
		Priority = priority;
		Allday = allday;
		Color = color;
		Remark = remark;
		NotePriority = notePriority;
		Line_Task = line_Task;
		Line_Project = line_Project;
		Line_File = line_File;
		Line_Group = line_Group;
		AHEAD_MINUTE = aHEAD_MINUTE;
		ISCALL = iSCALL;
		REPEATTYPE = rEPEATTYPE;
		REPEATENDTIME = rEPEATENDTIME;
		REPEATHZ = rEPEATHZ;
		REPEATWEEKVAL = rEPEATWEEKVAL;
		NoteId = noteId;
		SType = sType;
		IsJobSync = isJobSync;
		RepeatTimeVal = repeatTimeVal;
		RepeatMonthDayVal = repeatMonthDayVal;
		RepeatYearDayVal = repeatYearDayVal;
		NoteRoles = noteRoles;
		NoteContent = noteContent;
	}

	public CalendarInfo() {
	}

	// private int Allday;
	// private int Color;
	// private int ID;
	// private String OverTime;
	// private String StartTime;
	// private int Priority; // 优先权
	// private String Remark;
	// private int State;
	// private String Title;
	// private int Type;
	// private String Line_File;
	// private String Line_Project;
	// private String Line_Task;
	// private String NotePriority;
	// private int UserID;
	//
	// public String getLine_File() {
	// return Line_File;
	// }
	//
	// public void setLine_File(String line_File) {
	// Line_File = line_File;
	// }
	//
	// public String getLine_Project() {
	// return Line_Project;
	// }
	//
	// public void setLine_Project(String line_Project) {
	// Line_Project = line_Project;
	// }
	//
	// public String getLine_Task() {
	// return Line_Task;
	// }
	//
	// public void setLine_Task(String line_Task) {
	// Line_Task = line_Task;
	// }
	//
	// public String getNotePriority() {
	// return NotePriority;
	// }
	//
	// public void setNotePriority(String notePriority) {
	// NotePriority = notePriority;
	// }
	//
	// public int getUserID() {
	// return UserID;
	// }
	//
	// public void setUserID(int userID) {
	// UserID = userID;
	// }
	//
	// public int getAllday() {
	// return Allday;
	// }
	//
	// public void setAllday(int allday) {
	// Allday = allday;
	// }
	//
	// public int getColor() {
	// return Color;
	// }
	//
	// public void setColor(int color) {
	// Color = color;
	// }
	//
	// public int getID() {
	// return ID;
	// }
	//
	// public void setID(int iD) {
	// ID = iD;
	// }
	//
	// public String getOverTime() {
	// return OverTime;
	// }
	//
	// public void setOverTime(String overTime) {
	// OverTime = overTime;
	// }
	//
	// public String getStartTime() {
	// return StartTime;
	// }
	//
	// public void setStartTime(String startTime) {
	// StartTime = startTime;
	// }
	//
	// public int getPriority() {
	// return Priority;
	// }
	//
	// public void setPriority(int priority) {
	// Priority = priority;
	// }
	//
	// public String getRemark() {
	// return Remark;
	// }
	//
	// public void setRemark(String remark) {
	// Remark = remark;
	// }
	//
	// public int getState() {
	// return State;
	// }
	//
	// public void setState(int state) {
	// State = state;
	// }
	//
	// public String getTitle() {
	// return Title;
	// }
	//
	// public void setTitle(String title) {
	// Title = title;
	// }
	//
	// public int getType() {
	// return Type;
	// }
	//
	// public void setType(int type) {
	// Type = type;
	// }
	//
	// public CalendarInfo() {
	// super();
	// }
	//
	// @Override
	// public String toString() {
	// return "CalendarInfo [Allday=" + Allday + ", Color=" + Color + ", ID="
	// + ID + ", OverTime=" + OverTime + ", StartTime=" + StartTime
	// + ", Priority=" + Priority + ", Remark=" + Remark + ", State="
	// + State + ", Title=" + Title + ", Type=" + Type + "]";
	// }

	@SuppressLint("SimpleDateFormat")
	@Override
	public int compareTo(CalendarInfo arg0) {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Calendar nCal = java.util.Calendar.getInstance();
		try {
			cal.setTimeInMillis(time.parse(arg0.StartTime).getTime());
			nCal.setTimeInMillis(time.parse(this.StartTime).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (cal.getTimeInMillis() - nCal.getTimeInMillis() > 0) {
			return -1;
		} else if (cal.getTimeInMillis() - nCal.getTimeInMillis() < 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
