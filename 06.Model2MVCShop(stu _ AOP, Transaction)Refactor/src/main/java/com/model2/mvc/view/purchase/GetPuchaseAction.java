package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class GetPuchaseAction extends Action {

	public GetPuchaseAction() {
		System.out.println("[GetPuchaseAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[GetPuchaseAction execute() start...]");
		
		// 회원 상세주문조회
		int tranNo = Integer.parseInt(request.getParameter("tranNo"));
		System.out.println("tranNo : " + tranNo);

		PurchaseService service = new PurchaseServiceImpl();
		Purchase purchaseVO = service.getPurchase(tranNo);		
		System.out.println("getPurchase.jsp : " + purchaseVO);
		
		request.setAttribute("purchaseVO", purchaseVO);
		
		System.out.println("[GetPuchaseAction execute() end...]");
		return "forward:/purchase/getPurchase.jsp";
	}

}
