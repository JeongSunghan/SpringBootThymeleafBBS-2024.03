package com.example.abbs.controller;

import java.time.LocalDateTime;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorHandlingController implements ErrorController {
	
	@GetMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {
		// 에러 코드
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		// 에러 코드에 대한 상태 정보
		int statusCode = Integer.valueOf(status.toString());
		HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
		
		if (status != null) {
			String timestamp = LocalDateTime.now().toString().substring(0, 19).replace("T", " ");
			model.addAttribute("code", status.toString());
			model.addAttribute("msg", httpStatus.getReasonPhrase());
			model.addAttribute("timestamp", timestamp);
			
			if (statusCode == HttpStatus.NOT_FOUND.value())					// 404 error
				return "error/error404";
			if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())		// 500 error
				return "error/error500";
		}
		return "error/error";
	}
	
}