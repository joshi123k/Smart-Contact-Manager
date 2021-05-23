package com.smart.contact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contact.dao.UserRepository;
import com.smart.contact.entities.User;
import com.smart.contact.helper.Message;

import net.bytebuddy.utility.RandomString;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordAuth;

//	@Autowired
//	private JavaMailSender mailSender;

	@RequestMapping("/")
	public String index(Model m) {
		m.addAttribute("title", "Home- Smart Contact Manager");
		return "index";
	}

	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About- Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signUP(Model m) {
		m.addAttribute("title", "Register- Smart Contact Manager");
		m.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("You have not agreed terms and contions");
				throw new Exception("You have not agreed terms and contions");
			}

			if (result.hasErrors()) {
				m.addAttribute("user", user);
				return "register";
			}

			String randomCode = RandomString.make(64);

			user.setRole("ROLE_USER");
//			user.setVerificationCode(randomCode);
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordAuth.encode(user.getPassword()));

			User results = this.userRepository.save(user);
		
			m.addAttribute("user", new User());
			session.setAttribute("message", new Message("Register Successly!!", "alert-success"));
			return "register";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));
			return "register";
		}

	}

	
		
	

	@GetMapping("/signin")
	public String login(Model m) {
		m.addAttribute("title", "SignIn- Smart Contact Manager");
		return "login";
	}
}
