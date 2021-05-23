package com.smart.contact.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Orders")
public class MyOrders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long myOrderId;
	
	private String orderId;
	
	private String receipt;
	
	private String amount;
	
	private String status;

	
	@ManyToOne
	private User user;
	
	private String paymentId;

	public long getMyOrderId() {
		return myOrderId;
	}

	public void setMyOrderId(long myOrderId) {
		this.myOrderId = myOrderId;
	}
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	@Override
	public String toString() {
		return "MyOrders [myOrderId=" + myOrderId + ", orderId=" + orderId + ", receipt=" + receipt + ", amount="
				+ amount + ", user=" + user + ", paymentId=" + paymentId + "]";
	}
	
	
}
