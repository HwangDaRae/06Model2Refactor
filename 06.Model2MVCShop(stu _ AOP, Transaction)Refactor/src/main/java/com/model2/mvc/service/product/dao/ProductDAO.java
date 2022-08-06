package com.model2.mvc.service.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;

public class ProductDAO {

	public ProductDAO() {
		System.out.println("ProductDAO default constructor");
	}
	
	public int insertProduct(Product productVO) throws Exception {
		System.out.println("ProductDAO insertProduct(ProductVO productVO) start...");
		
		Connection con = DBUtil.getConnection();
		String sql = "INSERT INTO product VALUES (seq_product_prod_no.NEXTVAL,?,?,TO_CHAR(TO_DATE(?),'yyyymmdd'),?,?,sysdate,?)";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, productVO.getProdName());
		pstmt.setString(2, productVO.getProdDetail());
		pstmt.setString(3, productVO.getManuDate());
		pstmt.setInt(4, productVO.getPrice());
		pstmt.setString(5, productVO.getFileName());
		pstmt.setInt(6, productVO.getAmount());
		int i = pstmt.executeUpdate();
		
		DBUtil.close(con, pstmt);
		System.out.println("등록한 상품 정보 : " + productVO.toString());
		System.out.println("ProductDAO insertProduct(ProductVO productVO) end...");
		return i;		
	}
	
	public Product findProduct(int prodNo) throws Exception {
		System.out.println("ProductDAO findProduct(int prodNo) start...");
		System.out.println("찾을 상품의 고유번호 : " + prodNo);
		
		Connection con = DBUtil.getConnection();
		String sql = " SELECT p.*, t.tran_status_code tsc "
					+ " FROM product p, transaction t "
					+ " WHERE p.prod_no=t.prod_no(+) AND p.prod_no=? ";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setInt(1, prodNo);
		ResultSet rs = pst.executeQuery();

		Product productVO = new Product();
		while(rs.next()) {
			productVO.setProdNo(rs.getInt("PROD_NO"));
			productVO.setProdName(rs.getString("PROD_NAME"));
			productVO.setProdDetail(rs.getString("PROD_DETAIL"));
			productVO.setManuDate(rs.getString("MANUFACTURE_DAY"));
			productVO.setPrice(rs.getInt("PRICE"));
			productVO.setFileName(rs.getString("IMAGE_FILE"));
			productVO.setRegDate(rs.getDate("REG_DATE"));
			productVO.setAmount(rs.getInt("AMOUNT"));
			productVO.setProTranCode(rs.getString("TSC"));
		}
		
		DBUtil.close(con, pst, rs);		
		System.out.println("찾은 상품의 정보 : " + productVO.toString());		
		System.out.println("ProductDAO findProduct(int prodNo) end...");
		return productVO;
	}
	
	public Map<String, Object> getProductList(Search searchVO) throws Exception {
		System.out.println("ProductDAO getProductList(SearchVO searchVO) start...");
		Connection con = DBUtil.getConnection();
		
		//original sql
		String sql = " select vt.* "
				+ " from ( select ROW_NUMBER() OVER(PARTITION BY p.prod_no ORDER BY t.order_data desc) as r "
				+ " , p.amount, p.prod_no, p.prod_name, p.price, p.reg_date, nvl(tran_status_code, 0) as tcode "
				+ "	from transaction t, product p "
				+ "	where t.prod_no(+)=p.prod_no ) vt "
				+ " where r = 1 ";
		if(searchVO.getSearchCondition() != null && !searchVO.getSearchKeyword().trim().equals("")) {
			if(searchVO.getSearchCondition().equals("0")) {
				sql += " AND p.PROD_NO=" + searchVO.getSearchKeyword();
			}else if(searchVO.getSearchCondition().equals("1")) {
				sql += " AND UPPER(p.PROD_NAME) LIKE UPPER('%" + searchVO.getSearchKeyword() + "%') ";
			}else if(searchVO.getSearchCondition().equals("2")) {
				sql += " AND p.PRICE=" + searchVO.getSearchKeyword();
			}
		}
		System.out.println("ProductDAO Original sql : " + sql);
		
		if(searchVO.getPriceSort() != null && !searchVO.getPriceSort().equals("")) {
			sql += " ORDER BY price " + searchVO.getPriceSort();
			System.out.println("ProductDAO priceSort하는 sql : " + sql);
		}
		
		int total = getTotalCount(sql);
		System.out.println("ProductDAO 전체 레코드 수 : " + total);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("count", new Integer(total));
		
		sql = makeGetCurrentSql(sql, searchVO);
		System.out.println("ProductDAO currentPage 가져오는 sql : " + sql);
		
		PreparedStatement psmt = con.prepareStatement(sql);
		ResultSet rs = psmt.executeQuery();
		System.out.println("현재페이지 : "+searchVO.getCurruntPage()+", 화면에 나오는 레코드 수 : "+searchVO.getPageSize());

		ArrayList<Product> list = new ArrayList<Product>();
		if(total > 0) {
			while(rs.next()) {
				Product productVO = new Product();
				productVO.setProdNo(rs.getInt("prod_no"));
				productVO.setProdName(rs.getString("PROD_NAME"));
				productVO.setPrice(rs.getInt("PRICE"));
				productVO.setRegDate(rs.getDate("reg_date"));
				productVO.setProTranCode(rs.getString("tcode"));
				
				list.add(productVO);
			}
		}//end of if(total > 0)
		map.put("list", list);

		System.out.println("map().size() : "+ map.size()+", list.size() : "+list.size()+", list안의 데이터 : ");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		
		DBUtil.close(con, psmt, rs);
		System.out.println("ProductDAO getProductList(SearchVO searchVO) end...");
		return map;
	}
	
	public int updateProduct(Product productVO) throws Exception {
		System.out.println("ProductDAO updateProduct(ProductVO productVO) start...");
		System.out.println("수정할 상품 정보 : "+productVO.toString());
		
		Connection con = DBUtil.getConnection();
		String sql = "UPDATE PRODUCT set PROD_DETAIL=?,MANUFACTURE_DAY=TO_CHAR(TO_DATE(?),'YYYYMMDD'),PRICE=?,AMOUNT=? where PROD_NO=?";
		PreparedStatement pstmt;
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, productVO.getProdDetail());
		pstmt.setString(2, productVO.getManuDate());
		pstmt.setInt(3, productVO.getPrice());
		pstmt.setInt(4, productVO.getAmount());
		pstmt.setInt(5, productVO.getProdNo());
		int i = pstmt.executeUpdate();
		
		DBUtil.close(con, pstmt);		
		System.out.println("ProductDAO updateProduct(ProductVO productVO) end...");
		return i;
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
							+ "	FROM ( SELECT ROWNUM AS row_n, vt1.* "
							+ 		 " FROM ("+sql+") vt1 ) vt2 "
							+ " WHERE row_n BETWEEN "+((search.getCurruntPage()-1)*search.getPageSize()+1)+" AND "+search.getCurruntPage()*search.getPageSize();
		return currentSql;
	}
	
	// 장바구니 상품 재고수량 가져오기
	public Map<String, Object> getProdNoList() throws Exception {
		Connection con = DBUtil.getConnection();
		String sql = " SELECT amount FROM product WHERE prod_no=? ";
		PreparedStatement psmt = con.prepareStatement(sql);
		ResultSet rs = psmt.executeQuery();		
		
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Product> list = new ArrayList<Product>();
		while(rs.next()) {
			Product p = new Product();
			p.setAmount(rs.getInt(1));
			list.add(p);
		}
		map.put("list", list);
		return map;
	}

}


