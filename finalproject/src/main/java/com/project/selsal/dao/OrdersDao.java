package com.project.selsal.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.project.selsal.entities.Orderdetail;
import com.project.selsal.entities.Orders;

@Mapper
@Repository
public interface OrdersDao {
	public Orderdetail selectOne(int ordernum) throws Exception;
////
//	public int insertRow(Member member);
////
//	public int updateRow(Member member);
//	
//	public int updateAjax(Member member);
//
	public int insertRow(Orderdetail orderdetail);
	
	public int productCount();
	
	public int maxOrderNum();
	
	public ArrayList<Orders> selectAll();
	
	public ArrayList<Orderdetail>selectall();
//
//	public int levelUpdate(Member member);
//	
////
//	public int deleteAjax(String email);
}