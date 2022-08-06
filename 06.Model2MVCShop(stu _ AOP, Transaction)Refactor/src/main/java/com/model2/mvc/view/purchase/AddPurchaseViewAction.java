package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddPurchaseViewAction extends Action {

	public AddPurchaseViewAction() {
		System.out.println("[AddPurchaseViewAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[AddPurchaseViewAction execute() start...]");
		
		//상품번호에 맞는 ProductVO 찾고 request에 넣어 보내주기
		ProductService service = new ProductServiceImpl();
		Product productVO = service.getProduct(Integer.parseInt(request.getParameter("prod_no")));
		request.setAttribute("productVO", productVO);
		request.setAttribute("amount", request.getParameter("amount"));
		System.out.println(productVO.toString());
		
		System.out.println("[AddPurchaseViewAction execute() end...]");
		return "forward:/purchase/addPurchaseView.jsp";
	}

}
