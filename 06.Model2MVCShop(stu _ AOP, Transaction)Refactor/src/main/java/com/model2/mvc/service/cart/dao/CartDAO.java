package com.model2.mvc.service.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Cart;

public class CartDAO {

	public CartDAO() {
		System.out.println("CartDAO default constructor");
	}

	public List<Cart> getCartList(String user_id) throws Exception {
		System.out.println("CartDAO getCartList(String user_id) start...");
		
		Connection con = DBUtil.getConnection();
		String sql = " SELECT c.prod_no, c.user_id, c.image, c.product_name, c.product_detail, c.amount, c.price, p.amount as prod_amount "
				+ " FROM cart c, product p "
				+ " WHERE c.prod_no = p.prod_no AND user_id=? ";
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setString(1, user_id);
		ResultSet rs = psmt.executeQuery();
		
		List<Cart> list = new ArrayList<Cart>();
		while(rs.next()) {
			Cart cart = new Cart();
			cart.setProd_no(rs.getInt("PROD_NO"));
			cart.setUser_id(rs.getString("USER_ID"));
			cart.setImage(rs.getString("IMAGE"));
			cart.setProd_name(rs.getString("PRODUCT_NAME"));
			cart.setProd_detail(rs.getString("PRODUCT_DETAIL"));
			cart.setAmount(rs.getInt("AMOUNT"));
			cart.setPrice(rs.getInt("PRICE"));
			cart.setProd_amount(rs.getInt("PROD_AMOUNT"));
			list.add(cart);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		//전체 게시물 수 가져오기
		map.put("count", totalCount(sql, user_id) );
		
		DBUtil.close(con, psmt, rs);
		System.out.println("CartDAO getCartList(String user_id) end...");
		return list;
	}//end of getCartList(String user_id)
	
	public int totalCount(String originalSql, String user_id) throws Exception {
		Connection con = DBUtil.getConnection();
		String sql = " SELECT COUNT(*) FROM ( "+originalSql+" ) ";
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setString(1, user_id);
		ResultSet rs = psmt.executeQuery();
		
		int totalCount = 0;
		while(rs.next()) {
			totalCount = rs.getInt(1);
		}
		
		return totalCount;
	}//end of totalCount(String originalSql, String user_id)

	public void insertCart(Cart cart) throws Exception {
		System.out.println("CartDAO insertCart(Cart cart) strat...");
		
		Connection con = DBUtil.getConnection();
		String sql = " INSERT INTO cart VALUES(?, ?, ?, ?, ?, ?, ?) ";
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setInt(1, cart.getProd_no());
		psmt.setString(2, cart.getUser_id());
		psmt.setString(3, cart.getImage());
		psmt.setString(4, cart.getProd_name());
		psmt.setString(5, cart.getProd_detail());
		psmt.setInt(6, cart.getAmount());
		psmt.setInt(7, cart.getPrice());
		int i = psmt.executeUpdate();
		
		if(i==1) {
			System.out.println("장바구니에 상품추가 성공");
		}else {
			System.out.println("장바구니에 상품추가 실패");
		}
		
		DBUtil.close(con, psmt);
		System.out.println("CartDAO insertCart(Cart cart) end...");
	}//end of insertCart(Cart cart)

	public void deleteCart(Map<String, Object> map) throws Exception {
		System.out.println("CartDAO deleteCart(Map<String,Object> map) strat...");
		
		Connection con = DBUtil.getConnection();
		String user_id = (String)map.get("user_id");
		int[] prod_no_array = (int[]) map.get("deleteArray");
		
		System.out.println(user_id);
		
		for (int i = 0; i < prod_no_array.length; i++) {
			System.out.println("여기는 삭제 DAO : " + prod_no_array[i]);
		}
		
		//DELETE FROM cart WHERE prod_no IN ( 10031, 10003, 10025 ) AND user_id=?
		String sql = " DELETE FROM cart WHERE prod_no IN ( " + prod_no_array[0];
			if(prod_no_array.length > 1){
				for(int i=1; i<prod_no_array.length; i++){
					sql += " , " + prod_no_array[i];
				}
			}
		sql += " ) AND user_id=? ";
		System.out.println("deleteCart SQL : " + sql);
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setString(1, user_id);
		int i = psmt.executeUpdate();
		
		if(i==1) {
			System.out.println("장바구니에서 상품삭제 성공");
		}else {
			System.out.println("장바구니에서 상품삭제 실패");
		}

		DBUtil.close(con, psmt);
		System.out.println("CartDAO deleteCart(Map<String,Object> map) end...");
	}//end of deleteCart(Map<String, Object> map)

	public void updateAmount(Cart cart) throws Exception {
		System.out.println("CartDAO updateAmount(Cart cart) start...");
		
		Connection con = DBUtil.getConnection();
		String sql = " UPDATE cart SET amount=? WHERE prod_no=? AND user_id=? ";
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setInt(1, cart.getAmount());
		psmt.setInt(2, cart.getProd_no());
		psmt.setString(3, cart.getUser_id());
		int i = psmt.executeUpdate();

		if(i==1) {
			System.out.println("장바구니 수량변경 성공");
		}else {
			System.out.println("장바구니 수량변경 실패");
		}
		
		DBUtil.close(con, psmt);
		System.out.println("CartDAO updateAmount(Cart cart) end...");
	}//end of updateAmount(Cart cart)
}


