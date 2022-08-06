package com.model2.mvc.view.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class UpdateProductAction extends Action {

	public UpdateProductAction() {
		System.out.println("[UpdateProductAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[UpdateProductAction execute() start...]");
		Product productVO = new Product();
		
		System.out.println("³Ñ¹ö prodNo : " + request.getParameter("prodNo"));
		
		productVO.setProdNo(Integer.parseInt(request.getParameter("prodNo")));
		productVO.setProdDetail(request.getParameter("prodDetail"));
		productVO.setManuDate(request.getParameter("manuDate"));
		productVO.setAmount(Integer.parseInt(request.getParameter("amount")));
		productVO.setPrice(Integer.parseInt(request.getParameter("price")));
		
		ProductService service = new ProductServiceImpl();
		productVO = service.updateProduct(productVO);
		
		request.setAttribute("productVO", productVO);
		
		System.out.println("[UpdateProductAction execute() end...]");
		return "forward:/product/getProduct.jsp";
	}

}
