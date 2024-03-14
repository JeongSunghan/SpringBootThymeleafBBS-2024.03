package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.Anniversary;

public interface AnniversaryService {
	
	List<Anniversary> getAnnivListByDay(String uid, String sdate);

	List<Anniversary> getAnnivList(String uid, String startDay, String endDay);
	
	void insertAnniv(Anniversary anniv);
	
}