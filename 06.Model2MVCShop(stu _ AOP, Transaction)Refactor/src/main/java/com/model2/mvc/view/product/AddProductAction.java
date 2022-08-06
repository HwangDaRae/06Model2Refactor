package com.model2.mvc.view.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddProductAction extends Action {

	public AddProductAction() {
		System.out.println("[AddProductAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[AddProductAction execute() start...]");
		
		Product productVO = new Product();
		
		productVO.setProdName(request.getParameter("prodName"));
		productVO.setProdDetail(request.getParameter("prodDetail"));
		productVO.setManuDate(request.getParameter("manuDate"));
		productVO.setAmount(Integer.parseInt(request.getParameter("amount")));
		productVO.setPrice(Integer.parseInt(request.getParameter("price")));
		productVO.setFileName(request.getParameter("fileName"));
		
		ProductService service = new ProductServiceImpl();
		productVO = service.addProduct(productVO);
		System.out.println("¿©±â´Â Action after : " + productVO);
		
		request.setAttribute("productVO", productVO);
		
		System.out.println("[AddProductAction execute() end...]");
		return "forward:/product/addProduct.jsp";
	}

}
