package com.model2.mvc.service.product.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductDao;

@Repository("productDAOImpl")
public class ProductDaoImpl implements ProductDao {
	
	@Autowired
	@Qualifier("sqlSessionTemplate")
	SqlSession sqlSession;

	public ProductDaoImpl() {
		System.out.println("여기는 ProductDAOImpl default Constructor");
	}

	@Override
	public Product addProduct(Product productVO) throws Exception {
		int i = sqlSession.insert("ProductMapper.addProduct", productVO);
		System.out.println(i);
		if(i==1) {
			return productVO;
		}else {
			return null;
		}
	}

	@Override
	public Product getProduct(int prodNo) throws Exception {
		return sqlSession.selectOne("ProductMapper.findProduct", prodNo);
	}

	@Override
	public int getProductTotalCount(Search search) throws Exception {
		return sqlSession.selectOne("ProductMapper.totalCountProduct", search);
	}

	@Override
	public Map<String, Object> getProductList(Search searchVO) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Product> list = (List)sqlSession.selectList("ProductMapper.allProduct", searchVO);
		map.put("list", list);
		return map;
	}

	@Override
	public Product updateProduct(Product productVO) throws Exception {
		int i = sqlSession.update("ProductMapper.updateProduct", productVO);
		if(i==1) {
			System.out.println("상품 수정 성공");
			return sqlSession.selectOne("ProductMapper.findProduct", productVO.getProdNo());
		}else {
			System.out.println("상품 수정 실패");
			return null;
		}
	}

}
