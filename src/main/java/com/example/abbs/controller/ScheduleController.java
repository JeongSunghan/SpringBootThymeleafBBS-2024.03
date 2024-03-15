package com.example.abbs.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.abbs.entity.Anniversary;
import com.example.abbs.entity.SchDay;
import com.example.abbs.entity.Schedule;
import com.example.abbs.service.AnniversaryService;
import com.example.abbs.service.ScheduleService;
import com.example.abbs.util.SchedUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	@Autowired private ScheduleService schedSvc;
	@Autowired private AnniversaryService annivSvc;
	@Autowired private SchedUtil schedUtil;
	private String menu = "schedule";

	@GetMapping({"/calendar/{arrow}", "/calendar"})
	public String calendar(@PathVariable(required=false) String arrow, HttpSession session, Model model) {
		LocalDate today = LocalDate.now();
		int year = today.getYear();
		int month = today.getMonthValue();
		String date = "일 월 화 수 목 금 토".split(" ")[today.getDayOfWeek().getValue() % 7];
		String sessionMonthYear = (String) session.getAttribute("scheduleMonthYear");	// "2024.03"
		if (sessionMonthYear != null) {
			year = Integer.parseInt(sessionMonthYear.substring(0, 4));
			month = Integer.parseInt(sessionMonthYear.substring(5));
		}
		if (arrow != null) {
			switch(arrow) {
			case "left":
				month -= 1;
				if (month == 0) {
					year -= 1; month = 12;
				}
				break;
			case "right":
				month += 1;
				if (month == 13) {
					year += 1; month = 1;
				}
				break;
			case "left2":
				year -= 1; break;
			case "right2":
				year += 1; break;
			}
		}
		sessionMonthYear = String.format("%d.%02d", year, month);
		session.setAttribute("scheduleMonthYear", sessionMonthYear);
		String sessUid = (String) session.getAttribute("sessUid");
		
		List<SchDay> week = new ArrayList<>();
		List<List<SchDay>> calendar = new ArrayList<>();
		LocalDate startDay = LocalDate.parse(String.format("%d-%02d-01", year, month));
		int startDate = startDay.getDayOfWeek().getValue() % 7;
		LocalDate lastDay = startDay.withDayOfMonth(startDay.lengthOfMonth());
		int lastDate = lastDay.getDayOfWeek().getValue() % 7;
		
		// k는 날짜, i는 요일
		String sdate = null;
		// 첫번째 주
		if (startDate != 0) {		// 지난 달
			LocalDate prevSunDay = startDay.minusDays(startDate);
			int prevDay = prevSunDay.getDayOfMonth();
			int prevMonth = prevSunDay.getMonthValue();
			int prevYear = prevSunDay.getYear();
			for (int i = 0; i < startDate; i++) {
				sdate = String.format("%d%02d%02d", prevYear, prevMonth, prevDay+i);
				SchDay sd = schedSvc.generateSchDay(sessUid, prevDay+i, sdate, i, 1);
				week.add(sd);
			}
		}
		for (int i = startDate, k = 1; i < 7; i++, k++) {		// 이번 달
			sdate = String.format("%d%02d%02d", year, month, k);
			SchDay sd = schedSvc.generateSchDay(sessUid, k, sdate, i, 0);
			week.add(sd);
		}
		calendar.add(week);
		
		// 둘째 주부터 해당월의 마지막 날까지
		int day = 8 - startDate;
		for (int k = day, i = 0; k <= lastDay.getDayOfMonth(); k++, i++) {
			if (i % 7 == 0)
				week = new ArrayList<>();
			sdate = String.format("%d%02d%02d", year, month, k);
			SchDay sd = schedSvc.generateSchDay(sessUid, k, sdate, i % 7, 0);
			week.add(sd);
			if (i % 7 == 6)
				calendar.add(week);
		}
		
		// 다음 달 1일부터 그주 토요일까지
		if (lastDate != 6) {
			LocalDate nextDay = lastDay.plusDays(1);
			int nextMonth = nextDay.getMonthValue();
			int nextYear = nextDay.getYear();
			for (int i = lastDate + 1, k = 1; i < 7; i++, k++) {
				sdate = String.format("%d%02d%02d", nextYear, nextMonth, k);
				SchDay sd = schedSvc.generateSchDay(sessUid, k, sdate, i, 1);
				week.add(sd);
			}
			calendar.add(week);
		}
		
		model.addAttribute("calendar", calendar);
		model.addAttribute("today", today+"("+date+")");
		model.addAttribute("year", year);
		model.addAttribute("month", String.format("%02d", month));
		model.addAttribute("height", 600 / calendar.size());
		model.addAttribute("todaySdate", String.format("%d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth()));
		model.addAttribute("timeList", schedUtil.genTime());
		model.addAttribute("menu", menu);
		return "schedule/calendar";
	}

	@PostMapping("/insert")
	public String insert(String importance, String title, String startDate, String startTime, String endDate, String endTime,
							String place, String memo, HttpSession session) {
		int isImportant = (importance == null) ? 0 : 1;
		String sessUid = (String) session.getAttribute("sessUid");
		String sdate = startDate.replace("-", "");
		memo = (memo == null) ? "" : memo;
		Schedule schedule = new Schedule(sessUid, sdate, title, place, startTime, endTime, isImportant, memo);
//		System.out.println(schedule);
		schedSvc.insertSchedule(schedule);
		return "redirect:/schedule/calendar";
	}

	@ResponseBody
	@GetMapping("/detail/{sid}")
	public String detail(@PathVariable int sid) {
		Schedule sched = schedSvc.getSchedule(sid);
		JSONObject jSched = new JSONObject();
		jSched.put("sid", sid);
		jSched.put("title", sched.getTitle());
		jSched.put("place", sched.getPlace());
		jSched.put("sdate", sched.getSdate());
		jSched.put("startTime", sched.getStartTime());
		jSched.put("endTime", sched.getEndTime());
		jSched.put("isImportant", sched.getIsImportant());
		jSched.put("memo", sched.getMemo());
//		System.out.println(jSched.toString());
		return jSched.toString();
	}

	@PostMapping("/update")
	public String update(String importance, int sid, String title, String startDate, String startTime, String endDate, String endTime,
			String place, String memo, HttpSession session) {
		int isImportant = (importance == null) ? 0 : 1;
		String sessUid = (String) session.getAttribute("sessUid");
		String sdate = startDate.replace("-", "");
		memo = (memo == null) ? "" : memo;
		Schedule schedule = new Schedule(sid, sessUid, sdate, title, place, startTime, endTime, isImportant, memo);
		schedSvc.updateSchedule(schedule);
		return "redirect:/schedule/calendar";
	}

	@GetMapping("/delete/{sid}")
	public String delete(@PathVariable int sid) {
		schedSvc.deleteSchedule(sid);
		return "redirect:/schedule/calendar";
	}

	@PostMapping("/insertAnniv")
	public String insertAnniv(String holiday, String aname, String annivDate, HttpSession session) {
		int isHoliday = (holiday == null) ? 0 : 1;
		String adate = annivDate.replace("-", "");
		String sessUid = (String) session.getAttribute("sessUid");
		Anniversary anniversary = new Anniversary(sessUid, aname, adate, isHoliday);
		annivSvc.insertAnniv(anniversary);
		return "redirect:/schedule/calendar";
	}
	
}