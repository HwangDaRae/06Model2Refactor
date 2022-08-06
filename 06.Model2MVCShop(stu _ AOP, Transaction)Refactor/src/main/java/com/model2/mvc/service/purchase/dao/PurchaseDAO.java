package com.model2.mvc.service.purchase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;

public class PurchaseDAO {

	public PurchaseDAO() {
		System.out.println("PurchaseDao default constructor");
	}
	
	public Purchase findPurchase(int tranNo) throws Exception {
		System.out.println("PurchaseDao findPurchase(int tranNo) start...");
		Connection con = DBUtil.getConnection();
		String sql = " SELECT * FROM transaction WHERE tran_no=? ";
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setInt(1, tranNo);
		ResultSet rs = psmt.executeQuery();
		
		Product productVO = new Product();
		User userVO = new User();
		Purchase purchaseVO = new Purchase();
		while(rs.next()) {
			purchaseVO.setTranNo(rs.getInt("TRAN_NO"));
			productVO.setProdNo(rs.getInt("PROD_NO"));
			purchaseVO.setPurchaseProd(productVO);
			userVO.setUserId(rs.getString("BUYER_ID"));
			purchaseVO.setBuyer(userVO);
			purchaseVO.setPaymentOption(rs.getString("PAYMENT_OPTION"));
			purchaseVO.setReceiverName(rs.getString("RECEIVER_NAME"));
			purchaseVO.setReceiverPhone(rs.getString("RECEIVER_PHONE"));
			purchaseVO.setDivyAddr(rs.getString("DEMAILADDR"));
			purchaseVO.setDivyRequest(rs.getString("DLVY_REQUEST"));
			purchaseVO.setTranCode(rs.getString("TRAN_STATUS_CODE"));
			purchaseVO.setOrderDate(rs.getDate("ORDER_DATA"));
			purchaseVO.setDivyDate(rs.getDate("DLVY_DATE")+"");
			purchaseVO.setAmount(rs.getInt("AMOUNT"));
		}
		DBUtil.close(con, psmt, rs);
		return purchaseVO;
	}
	
	public List<Purchase> getPurchaseList(Search searchVO, String userID) throws Exception {
		System.out.println("PurchaseDao getPurchaseList(SearchVO searchVO) start...");
		
		Connection con = DBUtil.getConnection();
		System.out.println("userID : " + userID);
		String sql = " SELECT * FROM transaction WHERE buyer_id='"+userID+"' ORDER BY order_data DESC ";
		System.out.println("PurchaseDAO Original sql : " + sql);
		
		int total = getTotalCount(sql);
		System.out.println("PurchaseDAO 전체 레코드 수 : " + total);		

		ArrayList<Purchase> list = new ArrayList<Purchase>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("count", total);
		
		sql = makeGetCurrentSql(sql, searchVO);
		System.out.println("PurchaseDAO currentPage 가져오는 sql : " + sql);
		PreparedStatement ptmt = con.prepareStatement(sql);
		ResultSet rs = ptmt.executeQuery();

		Product productVO = new Product();
		User userVO = new User();
		if(total > 0) {
			while(rs.next()) {
				Purchase purchaseVO = new Purchase();
				purchaseVO.setTranNo(rs.getInt("TRAN_NO"));
				productVO.setProdNo(rs.getInt("PROD_NO"));
				purchaseVO.setPurchaseProd(productVO);
				userVO.setUserId(rs.getString("BUYER_ID"));
				purchaseVO.setBuyer(userVO);
				purchaseVO.setPaymentOption(rs.getString("PAYMENT_OPTION"));
				purchaseVO.setReceiverName(rs.getString("RECEIVER_NAME"));
				purchaseVO.setReceiverPhone(rs.getString("RECEIVER_PHONE"));
				purchaseVO.setDivyAddr(rs.getString("DEMAILADDR"));
				purchaseVO.setDivyRequest(rs.getString("DLVY_REQUEST"));
				purchaseVO.setTranCode(rs.getString("TRAN_STATUS_CODE"));
				purchaseVO.setDivyDate(rs.getDate("DLVY_DATE")+"");
				purchaseVO.setAmount(rs.getInt("AMOUNT"));
				
				list.add(purchaseVO);
				
				//if(!rs.next()) { // 첫번째 list add하고 한번 rs.next()를 했기에 두번째 list 건너뛰고 세번째 list add한다 
				//	break;
				//}
			}
		}
		map.put("list", list);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("PurchaseDAO list : "+list.get(i));
		}
		DBUtil.close(con, ptmt, rs);		
		return list;
	}
	
	public Map<String, Object> getSaleList(Search searchVO) throws Exception {
		System.out.println("PurchaseDao getSaleList(SearchVO searchVO) start...");
		return null;
	}
	
	public void insertPurchase(Purchase purchaseVO, Product productVO) throws Exception {
		System.out.println("PurchaseDao insertPurchase(PurchaseVO purchaseVO) start...");
		Connection conn = DBUtil.getConnection();
		String sql = " INSERT INTO transaction VALUES (seq_transaction_tran_no.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, '1', sysdate, ?, ?) ";
		System.out.println("insertPurchase : " + sql);
		
		PreparedStatement psmt = conn.prepareStatement(sql);
		psmt.setInt(1, purchaseVO.getPurchaseProd().getProdNo());
		psmt.setString(2, purchaseVO.getBuyer().getUserId());
		psmt.setString(3, purchaseVO.getPaymentOption());
		psmt.setString(4, purchaseVO.getReceiverName());
		psmt.setString(5, purchaseVO.getReceiverPhone());
		psmt.setString(6, purchaseVO.getDivyAddr());
		psmt.setString(7, purchaseVO.getDivyRequest());
		psmt.setString(8, purchaseVO.getDivyDate());
		psmt.setInt(9, purchaseVO.getAmount());
		
		int i = psmt.executeUpdate();
		if(i==1) {
			System.out.println("구매하기 성공");
		}
		DBUtil.close(conn, psmt);
		System.out.println("PurchaseDao insertPurchase(PurchaseVO purchaseVO) end...");
	}
	
	public void updatePurchase(Purchase purchaseVO) throws Exception {
		System.out.println("PurchaseDao updatePurchase(PurchaseVO purchaseVO) start...");
		Connection con = DBUtil.getConnection();
		
		String sql = " UPDATE transaction SET payment_option=?, receiver_name=?, receiver_phone=? "
				+ " , demailaddr=?, dlvy_request=?, dlvy_date=?, amount=? WHERE tran_no=? ";
		PreparedStatement psmt = con.prepareStatement(sql);
		psmt.setString(1, purchaseVO.getPaymentOption());
		psmt.setString(2, purchaseVO.getReceiverName());
		psmt.setString(3, purchaseVO.getReceiverPhone());
		psmt.setString(4, purchaseVO.getDivyAddr());
		psmt.setString(5, purchaseVO.getDivyRequest());
		psmt.setString(6, purchaseVO.getDivyDate());
		psmt.setInt(7, purchaseVO.getAmount());
		psmt.setInt(8, purchaseVO.getTranNo());
		int i = psmt.executeUpdate();
		
		DBUtil.close(con, psmt);
		
		if(i==1) {
			System.out.println("수정 성공");
		}else {
			System.out.println("수정 실패");
		}
	}
	
	public void updateTranCode(Purchase purchaseVO) throws Exception {
		System.out.println("PurchaseDao updateTranCode(PurchaseVO purchaseVO) start...");
		Connection con = DBUtil.getConnection();
		PreparedStatement psmt = null;
		String sql = " UPDATE transaction SET tran_status_code=? where ";
		int i = 0;
		if(purchaseVO.getTranNo() != 0) {
			sql += " tran_no=? ";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, purchaseVO.getTranCode());
			psmt.setInt(2, purchaseVO.getTranNo());
			i = psmt.executeUpdate();
		}else {
			sql += " prod_no=? ";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, purchaseVO.getTranCode());
			psmt.setInt(2, purchaseVO.getPurchaseProd().getProdNo());
			i = psmt.executeUpdate();
		}
		
		System.out.println("sql : " + sql);		
		System.out.println(purchaseVO.getTranCode());
		System.out.println(purchaseVO.getTranNo());
		
		DBUtil.close(con, psmt);
		
		if(i!=1) {
			System.out.println("상태코드 수정 실패");
		}else {
			System.out.println("상태코드 수정 성공");
		}
		System.out.println("PurchaseDao updateTranCode(PurchaseVO purchaseVO) end...");
	}
	
	//totalCount : 총 레코드 수 가져온다
	public int getTotalCount(String originalSql) throws Exception {
		Connection con = DBUtil.getConnection();
		String sql = "SELECT COUNT(*) FROM ("+originalSql+")";
		PreparedStatement psmt = con.prepareStatement(sql);
		ResultSet rs = psmt.executeQuery();
		
		int totalCount = 0;
		while(rs.next()) {
			totalCount = rs.getInt(1);
		}

		DBUtil.close(con, psmt, rs);
		return totalCount;
	}
	
	//currentPage의 레코드만 가져온다
	public String makeGetCurrentSql(String sql, Search search) throws Exception {
		String currentSql = " SELECT * "
							+ "	FROM ( SELECT ROWNUM r, vt1.* "
							+ 		 " FROM ("+sql+") vt1 ) vt2 "
							+ " WHERE r BETWEEN "+((search.getCurruntPage()-1)*search.getPageSize()+1)+" AND "+search.getCurruntPage()*search.getPageSize();
		return currentSql;
	}

}
