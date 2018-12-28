package cc.emw.mobile.entity;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UserSchedule;

/**
 * @author zrjt
 */
public class CalendarInfo extends UserSchedule implements
        Comparable<CalendarInfo>, Serializable {

    public List<ApiEntity.Role> mainUserList;
    public String HandlerTitle;
    public String HandlerRemark;

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

    @SuppressLint("SimpleDateFormat")
    @Override
    public int compareTo(CalendarInfo arg0) {
        SimpleDateFormat time = null;
        if (arg0.StartTime.length() > 16)
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        else
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Calendar nCal = java.util.Calendar.getInstance();
        try {
            cal.setTimeInMillis(time.parse(arg0.StartTime).getTime());
            nCal.setTimeInMillis(time.parse(this.StartTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cal.getTimeInMillis() - nCal.getTimeInMillis() > 0) {
            return 1;
        } else if (cal.getTimeInMillis() - nCal.getTimeInMillis() < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
