package com.smart.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.contact.entities.MyOrders;

public interface MyOrdersRepository extends JpaRepository<MyOrders, Long> {

	public MyOrders findByOrderId(String orderid);
}
