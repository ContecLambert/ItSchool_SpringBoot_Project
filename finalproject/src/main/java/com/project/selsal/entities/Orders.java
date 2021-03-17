package com.project.selsal.entities;

import org.springframework.stereotype.Component;

@Component
public class Orders {
	private int ordernum;
	private String email;
	private String orderadd;
	private String orderadddetail;
	private int totprice;
	private String datedate;
	private int quantity;
	
	
	
	
	

	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(int ordernum) {
		this.ordernum = ordernum;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrderadd() {
		return orderadd;
	}
	public void setOrderadd(String orderadd) {
		this.orderadd = orderadd;
	}
	public String getOrderadddetail() {
		return orderadddetail;
	}
	public void setOrderadddetail(String orderadddetail) {
		this.orderadddetail = orderadddetail;
	}
	public int getTotprice() {
		return totprice;
	}
	public void setTotprice(int totprice) {
		this.totprice = totprice;
	}
	public String getDatedate() {
		return datedate;
	}
	public void setDatedate(String datedate) {
		this.datedate = datedate;
	}
	



}
