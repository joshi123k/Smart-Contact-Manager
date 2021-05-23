package com.smart.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.contact.entities.Contact;
import com.smart.contact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	@Query("from Contact  as d where d.user.id=:userId")
	public Page<Contact> findContactsById(@Param("userId") int userId,Pageable pageable);
	
	public List<Contact> findByNameContainingAndUser(String name,User user);

}
