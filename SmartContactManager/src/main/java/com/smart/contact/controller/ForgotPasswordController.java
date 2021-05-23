package com.smart.contact.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contact.dao.UserRepository;
import com.smart.contact.entities.User;
import com.smart.contact.helper.Message;
import com.smart.contact.service.EmailService;

@Controller
public class ForgotPasswordController {

	
	
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	

	Random rd = new Random(1000);

	@GetMapping("/forgot")
	public String openForgot(Model m) {
		m.addAttribute("title", "Forgot Password- Smart Contact Manager");
		return "forgot";
	}

	@PostMapping("/send-otp")
	public String sendEmail(@RequestParam("email") String email, Model m, HttpSession session) {

		m.addAttribute("title", "Otp Send");

		int otp = rd.nextInt(9999999);
		
		User user =this.userRepository.getUserByUserName(email);

		String subject = "send otp from SCM";

		String message = "<div style='border:1px solid #e2e2e2'>"
				+ "<h1>"
				+ "<b>"
				+ "OTP = "+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		
		

		String to = email;
		
		boolean emailSend;
		
		if(user!=null)
		{

		 emailSend=emailService.sendEmail(message, subject, to);
		 
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			session.setAttribute("message", new Message("Send OTP  Successfully!!", "alert-success"));
			return "change_password";
		} else {
			
			session.setAttribute("message", new Message("Please Enter Your Register email !!", "alert-danger"));
			return "forgot";
		}
	}
	
	@PostMapping("/verify_otp")
	public String verifyOtp(@RequestParam("otp") String otp1,@RequestParam("newpassword") String newpassword,HttpSession session)
	{
		
		String oldotp=(String) session.getAttribute("otp");
		String myemail=(String) session.getAttribute("email");
		User user =this.userRepository.getUserByUserName(myemail);
		
		if(oldotp!=otp1)
		{
			session.setAttribute("message", new Message(" Wrong OTP !!", "alert-danger"));
			return "change_password";
		}else {
			user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			
			this.userRepository.save(user);
			
			session.setAttribute("message", new Message("Password Change Successfully!!", "alert-success"));
		    return "login";
		}
	}

}