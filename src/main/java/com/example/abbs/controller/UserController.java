package com.example.abbs.controller;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.abbs.entity.User;
import com.example.abbs.service.UserService;
import com.example.abbs.util.AsideUtil;
import com.example.abbs.util.ImageUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired private UserService uSvc;
	@Autowired private ImageUtil imageUtil;
	@Autowired private AsideUtil asideUtil;
	@Autowired private ResourceLoader resourceLoader;
	@Value("${spring.servlet.multipart.location}") private String uploadDir;

	@GetMapping("/register")
	public String registerForm() {
		return "user/register";
	}
	
	@PostMapping("/register") 
	public String registerProc(MultipartHttpServletRequest req, Model model,
			String uid, String pwd, String pwd2, String uname, String email,
			String github, String insta, String location) {
		String filename = null;
		MultipartFile filePart = req.getFile("profile");
		
		if (uSvc.getUserByUid(uid) != null) {
			model.addAttribute("msg", "사용자 ID가 중복되었습니다.");
			model.addAttribute("url", "/abbs/user/register");
			return "common/alertMsg";
		}
		if (pwd.equals(pwd2) && pwd != null) {
			if (filePart.getContentType().contains("image")) {
				filename = filePart.getOriginalFilename();
				String path = uploadDir + "profile/" + filename;
				try {
					filePart.transferTo(new File(path));
				} catch (Exception e) {
					e.printStackTrace();
				}
				filename = imageUtil.squareImage(uid, filename);
			}
			User user = new User(uid, pwd, uname, email, filename, github, insta, location);
			uSvc.registerUser(user);
			model.addAttribute("msg", "등록을 마쳤습니다. 로그인하세요.");
			model.addAttribute("url", "/abbs/user/login");
			return "common/alertMsg";
		} else {
			model.addAttribute("msg", "패스워드 입력이 잘못되었습니다.");
			model.addAttribute("url", "/abbs/user/register");
			return "common/alertMsg";
		}
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "user/login";
	}
	
	@PostMapping("/login")
	public String loginProc(String uid, String pwd, HttpSession session, Model model) {
		int result = uSvc.login(uid, pwd);
		switch(result) {
		case UserService.CORRECT_LOGIN:
			User user = uSvc.getUserByUid(uid);
			session.setAttribute("sessUid", uid);
			session.setAttribute("sessUname", user.getUname());
			session.setAttribute("profile", user.getProfile());
			session.setAttribute("email", user.getEmail());
			session.setAttribute("github", user.getGithub());
			session.setAttribute("insta", user.getInsta());
			session.setAttribute("location", user.getLocation());
			// 상태 메세지
			// c:/Temp/abbs/data/todayQuote.txt
//			String quoteFile = uploadDir + "data/todayQuote.txt";
			// resources/static/data/todayQuote.txt
			Resource resource = resourceLoader.getResource("classpath:/static/data/todayQuote.txt");
			String quoteFile = null;
			try {
				quoteFile = resource.getURI().getPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String stateMsg = asideUtil.getTodayQuote(quoteFile);
			session.setAttribute("stateMsg", stateMsg);
			// 환영 메세지
			log.info("Info Login: {}, {}", uid, user.getUname());
			model.addAttribute("msg", user.getUname()+"님 환영합니다.");
			model.addAttribute("url", "/abbs/board/list");
			break;
			
		case UserService.USER_NOT_EXIST:
			model.addAttribute("msg", "ID가 없습니다. 회원가입 페이지로 이동합니다.");
			model.addAttribute("url", "/abbs/user/register");
			break;
		
		case UserService.WRONG_PASSWORD:
			model.addAttribute("msg", "패스워드 입력이 잘못되었습니다. 다시 입력하세요.");
			model.addAttribute("url", "/abbs/user/login");
		}
		return "common/alertMsg";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/user/login";
	}
	
}
