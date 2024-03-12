package com.example.abbs.entity;

public class Schedule {
	private int sid;
	private String uid;
	private String sdate;
	private String title;
	private String place;
	private String startTime;
	private String endTime;
	private int isImportant;
	private String memo;
	
	public Schedule() { }
	public Schedule(int sid, String uid, String sdate, String title, String place, String startTime, String endTime,
			int isImportant, String memo) {
		this.sid = sid;
		this.uid = uid;
		this.sdate = sdate;
		this.title = title;
		this.place = place;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isImportant = isImportant;
		this.memo = memo;
	}
	
	@Override
	public String toString() {
		return "Schedule [sid=" + sid + ", uid=" + uid + ", sdate=" + sdate + ", title=" + title + ", place=" + place
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", isImportant=" + isImportant + ", memo="
				+ memo + "]";
	}
	
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getSdate() {
		return sdate;
	}
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getIsImportant() {
		return isImportant;
	}
	public void setIsImportant(int isImportant) {
		this.isImportant = isImportant;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}
