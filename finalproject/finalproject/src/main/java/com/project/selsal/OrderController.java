package com.project.selsal;

import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.selsal.dao.OrdersDao;
import com.project.selsal.dao.ProductDao;
import com.project.selsal.entities.Member;
import com.project.selsal.entities.Orderdetail;
import com.project.selsal.entities.Orders;
import com.project.selsal.entities.Product;




@Controller
public class OrderController {
	@Autowired
	private SqlSession sqlSession;
	@Autowired
	Orders orders;
	@Autowired
	Orderdetail orderdetail;
	@Autowired
	Product product;
	
	
	
	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(Locale locale, Model model) throws Exception {
		ProductDao dao = sqlSession.getMapper(ProductDao.class);
		ArrayList<Product> products = dao.selectAll();
		model.addAttribute("products",products);
		OrdersDao orderdao = sqlSession.getMapper(OrdersDao.class);
		int orderNum = orderdao.maxOrderNum();
		System.out.println(orderNum);
		model.addAttribute("ordernum",orderNum);
		return "order/order_insert";
	}
	
	@RequestMapping(value = "/orderInsert", method = RequestMethod.POST)
	@ResponseBody
	public String orderInsert(@RequestParam int procode,@RequestParam int qty,@RequestParam int ordernum) {
		OrdersDao dao = sqlSession.getMapper(OrdersDao.class);
		orderdetail.setOrdernum(ordernum);
		orderdetail.setQty(qty);
		orderdetail.setProcode(procode);
		dao.insertRow(orderdetail);
		return "";
	}
	@RequestMapping(value = "/productCount", method = RequestMethod.POST)
	@ResponseBody
	public int productCount() {
		OrdersDao dao = sqlSession.getMapper(OrdersDao.class);
		int result = dao.productCount();
		return result;
	}
	@RequestMapping(value = "/orderConfirm", method = RequestMethod.POST)
	@ResponseBody
	public String orderConfirm(@RequestParam int ordernum,HttpSession session) {
		OrdersDao dao = sqlSession.getMapper(OrdersDao.class);
		String email = (String) session.getAttribute("sessionemail");
		Member member = dao.selectAddress(email);
		String address = "우편번호:"+ member.getZipcode()+" / "+member.getAddress()+" , "+member.getDetailaddress(); 
		dao.orderInsert(ordernum, email,address);
		return "";
	}
	
}
