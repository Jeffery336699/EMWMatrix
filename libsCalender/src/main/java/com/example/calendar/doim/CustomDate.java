package com.example.calendar.doim;

import java.io.Serializable;
import java.util.Calendar;

import com.example.caledar.util.DateUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 
 * @author zrjt
 * 
 */
public class CustomDate implements Serializable {

	private static final long serialVersionUID = 1L;
	public int year;
	public int month;
	public int day;
	public int week;
	public int endYear;
	public int endDay;
	public int endMonth;
	public Calendar start;
	public Calendar end;
	
	public CustomDate(int year, int month, int day) {
		if (month > 12) {
			month = 1;
			year++;
		} else if (month < 1) {
			month = 12;
			year--;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public CustomDate() {
		this.year = DateUtil.getYear();
		this.month = DateUtil.getMonth();
		this.day = DateUtil.getCurrentMonthDay();
	}

	public static CustomDate modifiDayForObject(CustomDate date, int day) {
		CustomDate modifiDate = new CustomDate(date.year, date.month, day);
		return modifiDate;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	@Override
	public String toString() {
		return "CustomDate [year=" + year + ", month=" + month + ", day=" + day
				+ ", week=" + week;
	}

}
