package com.example.abbs.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AbbsFilter extends HttpFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		session.setMaxInactiveInterval(10 * 3600); 		// 10 시간
		
		String uri = httpRequest.getRequestURI();
		if (uri.contains("board"))
			session.setAttribute("menu", "board");
		else if (uri.contains("user"))
			session.setAttribute("menu", "user");
		else if (uri.contains("schedule"))
			session.setAttribute("menu", "schedule");
		else
			session.setAttribute("menu", "");
		
		// 로그인이 필요한 URL
		String[] urlPatterns = {"/board", "/aside", "/file", "/schedule",
								"/user/list", "/user/update", "/user/delete"};
		String sessUid = (String) session.getAttribute("sessUid");
		for (String pattern: urlPatterns) {
			if (uri.contains(pattern)) {
				if (sessUid == null || sessUid.equals(""))
					httpResponse.sendRedirect("/abbs/user/login");
				break;
			}
		}
		
		chain.doFilter(request, response);
	}
}