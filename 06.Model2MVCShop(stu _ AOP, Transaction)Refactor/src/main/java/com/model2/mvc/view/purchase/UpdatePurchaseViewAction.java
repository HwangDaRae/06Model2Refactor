package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class UpdatePurchaseViewAction extends Action {

	public UpdatePurchaseViewAction() {
		System.out.println("[UpdatePurchaseViewAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[UpdatePurchaseViewAction execute() start...]");	
		
		int tranNo = Integer.parseInt(request.getParameter("tranNo"));
		
		PurchaseService service = new PurchaseServiceImpl();
		Purchase purchaseVO = service.getPurchase(tranNo);
		ProductService p_service = new ProductServiceImpl();
		Product productVO = p_service.getProduct(tranNo);
		
		request.setAttribute("purchaseVO", purchaseVO);
		request.setAttribute("productVO", productVO);
		productVO.setAmount( productVO.getAmount() - purchaseVO.getAmount() );
		System.out.println("확인 : " + purchaseVO);
		System.out.println("확인 : " + productVO);
		
		System.out.println("[UpdatePurchaseViewAction execute() end...]");
		return "forward:/purchase/updatePurchaseView.jsp";
	}

}
