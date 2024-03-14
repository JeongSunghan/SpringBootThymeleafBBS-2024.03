package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.SchDay;
import com.example.abbs.entity.Schedule;

public interface ScheduleService {

	Schedule getSchedule(int sid);
	
	List<Schedule> getSchedList(String uid, String startDay, String endDay);
	
	List<Schedule> getSchedListByDay(String uid, String sdate);
	
	void insertSchedule(Schedule schedule);
	
	void updateSchedule(Schedule schedule);
	
	void deleteSchedule(int sid);
	
	SchDay generateSchDay(String uid, int day, String sdate, int date, int isOtherMonth);
	
}