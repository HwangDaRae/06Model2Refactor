package com.model2.mvc.view.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class UpdateProductViewAction extends Action {

	public UpdateProductViewAction() {
		System.out.println("[UpdateProductViewAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[UpdateProductViewAction execute() start...]");
		String menu = request.getParameter("menu");
		Product productVO = new Product();
		
		ProductService service = new ProductServiceImpl();
		productVO = service.getProduct(Integer.parseInt(request.getParameter("prodNo")));
		
		request.setAttribute("productVO", productVO);
		request.setAttribute("menu", menu);
		System.out.println(productVO);

		System.out.println("[UpdateProductViewAction execute() end...]");
		
		if( menu.equals("manage") && productVO.getProTranCode() == null ) {
			return "forward:/product/updateProductView.jsp";
		}else {
			return "forward:/product/getProduct.jsp";
		}
	}

}
