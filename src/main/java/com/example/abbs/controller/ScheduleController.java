package com.example.abbs.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.abbs.entity.SchDay;
import com.example.abbs.service.ScheduleService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	// 스케줄 서비스를 주입하기 위한 어노테이션 사용
	@Autowired
	private ScheduleService schedSvc;
	// 메뉴 관련 정보를 저장하기 위한 필드
	private String menu = "schedule";

	/**
	 * 달력 페이지로 이동하는 메소드
	 * 
	 * @param arrow   이전 또는 다음 버튼 클릭 여부를 나타내는 파라미터
	 * @param session 세션 객체
	 * @param model   모델 객체
	 * @return 달력 페이지 뷰 이름
	 */
	@GetMapping({ "/calendar/{arrow}", "/calendar" })
	public String calendar(@PathVariable(required = false) String arrow, HttpSession session, Model model) {
		// 현재 날짜 정보 가져오기
		LocalDate today = LocalDate.now();
		int year = today.getYear();
		int month = today.getMonthValue();
		String date = "일 월 화 수 목 금 토".split(" ")[today.getDayOfWeek().getValue() % 7];
		String sessionMonthYear = (String) session.getAttribute("scheduleMonthYear"); // "2024.03"
		if (sessionMonthYear != null) {
			year = Integer.parseInt(sessionMonthYear.substring(0, 4));
			month = Integer.parseInt(sessionMonthYear.substring(5));
		}
		// 이전 또는 다음 버튼 클릭 시 날짜 변경 처리
		if (arrow != null) {
			switch (arrow) {
			case "left":
				month -= 1;
				if (month == 0) {
					year -= 1;
					month = 12;
				}
				break;
			case "right":
				month += 1;
				if (month == 13) {
					year += 1;
					month = 1;
				}
				break;
			case "left2":
				year -= 1;
				break;
			case "right2":
				year += 1;
				break;
			}
		}
		sessionMonthYear = String.format("%d.%02d", year, month);
		session.setAttribute("scheduleMonthYear", sessionMonthYear);
		String sessUid = (String) session.getAttribute("sessUid");

		// 달력 데이터를 담을 리스트 초기화
		// 주간 리스트를 담을 ArrayList 생성
		List<SchDay> week = new ArrayList<>();
		// 달력을 담을 2차원 ArrayList 생성
		List<List<SchDay>> calendar = new ArrayList<>();
		// 현재 연도와 월로부터 첫째 날을 가져옴
		LocalDate startDay = LocalDate.parse(String.format("%d-%02d-01", year, month));
		// 해당 월의 첫째 날의 요일을 가져와서 7로 나눈 나머지를 구하고 저장
		int startDate = startDay.getDayOfWeek().getValue() % 7;
		// 해당 월의 마지막 날짜를 가져옴
		LocalDate lastDay = startDay.withDayOfMonth(startDay.lengthOfMonth());
		// 해당 월의 마지막 날짜의 요일을 가져와서 7로 나눈 나머지를 구하고 저장
		int lastDate = lastDay.getDayOfWeek().getValue() % 7;

		String sdate = null;
		// 첫째 주 처리
		if (startDate != 0) { // 지난 달
			LocalDate prevSunDay = startDay.minusDays(startDate);
			int prevDay = prevSunDay.getDayOfMonth();
			int prevMonth = prevSunDay.getMonthValue();
			int prevYear = prevSunDay.getYear();
			for (int i = 0; i < startDate; i++) {
				// 이전 달의 날짜로 sdate를 설정하고, 이를 통해 SchDay 객체 생성
				sdate = String.format("%d%02d%02d", prevYear, prevMonth, prevDay + i);
				SchDay sd = schedSvc.generateSchDay(sessUid, prevDay + i, sdate, i, 1);
				// 생성된 SchDay 객체를 주간 리스트에 추가
				week.add(sd);
			}
		}
		// 이번 달의 날짜로 주간 리스트 채우기
		for (int i = startDate, k = 1; i < 7; i++, k++) {
			// 현재 달의 날짜로 sdate를 설정하고, 이를 통해 SchDay 객체 생성
			sdate = String.format("%d%02d%02d", year, month, k);
			SchDay sd = schedSvc.generateSchDay(sessUid, k, sdate, i, 0);
			// 생성된 SchDay 객체를 주간 리스트에 추가
			week.add(sd);
		}
		// 이번 달의 첫째 주(지난 달 포함)를 달력에 추가
		calendar.add(week);

		// 이번 달의 나머지 주간 데이터 추가
		int day = 8 - startDate; // 이번 달의 시작일
		for (int k = day, i = 0; k <= lastDay.getDayOfMonth(); k++, i++) {
			// 한 주가 시작될 때마다 새로운 주간 리스트 생성
			if (i % 7 == 0)
				week = new ArrayList<>();
			// 현재 달의 날짜로 sdate를 설정하고, 이를 통해 SchDay 객체 생성
			sdate = String.format("%d%02d%02d", year, month, k);
			SchDay sd = schedSvc.generateSchDay(sessUid, k, sdate, i % 7, 0);
			// 생성된 SchDay 객체를 주간 리스트에 추가
			week.add(sd);
			// 한 주가 완성되면 해당 주간 리스트를 달력에 추가
			if (i % 7 == 6)
				calendar.add(week);
		}

		// 다음 달의 첫째 주 데이터 추가
		if (lastDate != 6) {
			LocalDate nextDay = lastDay.plusDays(1);
			int nextMonth = nextDay.getMonthValue();
			int nextYear = nextDay.getYear();
			for (int i = lastDate + 1, k = 1; i < 7; i++, k++) {
				// 다음 달의 날짜로 sdate를 설정하고, 이를 통해 SchDay 객체 생성
				sdate = String.format("%d%02d%02d", nextYear, nextMonth, k);
				SchDay sd = schedSvc.generateSchDay(sessUid, k, sdate, i, 1);
				// 생성된 SchDay 객체를 주간 리스트에 추가
				week.add(sd);
			}
			// 다음 달의 첫째 주를 달력에 추가
			calendar.add(week);
		}

		// 모델에 데이터 전달
		// 달력 데이터를 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("calendar", calendar);
		// 오늘 날짜와 요일을 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("today", today + "(" + date + ")");
		// 현재 연도를 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("year", year);
		// 현재 월을 두 자리 숫자로 포맷하여 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("month", String.format("%02d", month));
		// 달력의 높이를 계산하여 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("height", 600 / calendar.size());
		// 오늘 날짜의 문자열을 YYYYMMDD 형식으로 포맷하여 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("todaySdate", String.format("%d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth()));
		// 메뉴 정보를 뷰로 전달하기 위해 모델에 추가
		model.addAttribute("menu", menu);

		// 달력 뷰 페이지로 이동
		return "schedule/calendar";

	}
}
