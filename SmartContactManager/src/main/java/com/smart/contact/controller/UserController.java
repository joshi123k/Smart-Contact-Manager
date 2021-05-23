package com.smart.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import com.razorpay.*;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.contact.dao.ContactRepository;
import com.smart.contact.dao.MyOrdersRepository;
import com.smart.contact.dao.UserRepository;
import com.smart.contact.entities.Contact;
import com.smart.contact.entities.MyOrders;
import com.smart.contact.entities.User;
import com.smart.contact.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MyOrdersRepository myOrdersRepository;

	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		String name = principal.getName();
		System.out.println(name);

		User user = userRepository.getUserByUserName(name);
		m.addAttribute("user", user);
		System.out.println(user);

	}

	@RequestMapping("/index")
	public String index(Model m) {
		m.addAttribute("title", "Dashboard-Smart Contact Manager");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addContactForm(Model m) {
		m.addAttribute("title", "Add Contact-Smart Contact Manager");
		m.addAttribute("contact", new Contact());
		return "normal/addContact";
	}

	@PostMapping("/process-contact")
	public String addContact(@ModelAttribute Contact contact, @RequestParam("profilePic") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();

			User user = userRepository.getUserByUserName(name);

			if (file.isEmpty()) {

				System.out.println("image not found");
				contact.setProfile("contact.png");

			} else {
				contact.setProfile(file.getOriginalFilename());

				File files = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(files.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Uploaded");

				session.setAttribute("message", new Message("Contct is Added! Add More", "success"));
			}

			contact.setUser(user);

			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("saved");

		} catch (Exception e) {

			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();

			session.setAttribute("message", new Message("Something went wrong! Try Again", "danger"));
		}

		return "normal/addContact";
	}

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "View Contacts-Smart Contact Manager");

		String name = principal.getName();

		User user = userRepository.getUserByUserName(name);

		Pageable pageable = PageRequest.of(page, 4);

		Page<Contact> contacts = this.contactRepository.findContactsById(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalpages", contacts.getTotalPages());
		return "normal/showContacts";
	}

	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model m, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);

		Contact contact = contactOptional.get();

		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getName() + " " + "Smart Contact Manager");

		}
		return "normal/contact_Details";
	}

	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model m, HttpSession session, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);

		Contact contact = contactOptional.get();

		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);

		user.getContacts().remove(contact);
		
		this.userRepository.save(user);

		session.setAttribute("message", new Message("Contact deleted Successfully", "success"));

		return "redirect:/user/show-contacts/0";
	}

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title", "Update-Smart Contact Manager");

		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profilePics") MultipartFile file,
			HttpSession session, Model m, Principal principal) {

		try {

			Contact oldContactDetail = this.contactRepository.findById(contact.getId()).get();

			if (!file.isEmpty()) {

				// delete file

				File deleteFile = new ClassPathResource("static/images").getFile();
				File file2 = new File(deleteFile, oldContactDetail.getProfile());
				file2.delete();

				// Update image

				File files = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(files.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setProfile(file.getOriginalFilename());

			} else {
				contact.setProfile(oldContactDetail.getProfile());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact is Updated Successfully", "success"));

		} catch (Exception e) {

			e.printStackTrace();
		}
		return "redirect:/user/" + contact.getId() + "/contact/";
	}
	
	@GetMapping("/profiles")
	public String yourProfile(Model m)
	{
		
		m.addAttribute("title", "Profile - Smart Contact Manager");
		return "normal/profiles";
	}
	
	@GetMapping("/settings")
	public String openResetForm(Model m) {
		
		m.addAttribute("title","Reset Password - Smart Contact Manager" );
		return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession httpSession)
	{
		String name = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(name);
		String old_password = currentUser.getPassword();
		if(this.bCryptPasswordEncoder.matches(oldPassword, old_password))
		{
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			httpSession.setAttribute("message", new Message("Password Change Successfully", "success"));
		}
		else 
			{
			   httpSession.setAttribute("message", new Message("Please Enter Correct Password", "danger"));
			   return "redirect:/user/settings";
			}
		
		return "redirect:/user/index";
	}
	
	// Create Order
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception
	{
		System.out.println("Hey Order function Excuted"+data);
		int amt=Integer.parseInt(data.get("amount").toString());
		
		var client=new RazorpayClient("rzp_test_oedAGEHXzWXpTH", "BpYbxzlNAx4p4fDDMUezRBQy");
		
		JSONObject options = new JSONObject();
		options.put("amount", amt*100);
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		
		// Create Order
		
		Order order = client.Orders.create(options);
		
		System.out.println(order);
		
		// Save details in DB
		
		MyOrders myOrders = new MyOrders();
		
		myOrders.setAmount(order.get("amount")+"");
		
		myOrders.setOrderId(order.get("id"));
		
		myOrders.setStatus("created");
		
		myOrders.setUser(this.userRepository.getUserByUserName(principal.getName()));
		
		myOrders.setReceipt("receipt");
		
		this.myOrdersRepository.save(myOrders);
		
		return order.toString();
	
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> orderUpdate(@RequestBody Map<String, Object> data)
	{
		MyOrders myorder = this.myOrdersRepository.findByOrderId(data.get("order_id").toString());
		
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		
		this.myOrdersRepository.save(myorder);
		
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}

}
