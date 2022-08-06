package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class UpdatePurchaseAction extends Action {

	public UpdatePurchaseAction() {
		System.out.println("[UpdatePurchaseAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[UpdatePurchaseAction execute() start...]");
		
		int tranNo = Integer.parseInt(request.getParameter("tranNo"));
		String paymentOption = request.getParameter("paymentOption");
		String receiverName = request.getParameter("receiverName");
		String receiverPhone = request.getParameter("receiverPhone");
		String receiverAddr = request.getParameter("receiverAddr");
		String receiverRequest = request.getParameter("receiverRequest");
		String divyDate = request.getParameter("divyDate");
		int amount = Integer.parseInt(request.getParameter("amount"));
		
		Purchase purchaseVO = new Purchase();
		purchaseVO.setTranNo(tranNo);
		purchaseVO.setPaymentOption(paymentOption);
		purchaseVO.setReceiverName(receiverName);
		purchaseVO.setReceiverPhone(receiverPhone);
		purchaseVO.setDivyAddr(receiverAddr);
		purchaseVO.setDivyRequest(receiverRequest);
		purchaseVO.setDivyDate(divyDate);
		purchaseVO.setAmount(amount);
		
		System.out.println("UpdatePurchaseAction : " + purchaseVO);
		
		PurchaseService service = new PurchaseServiceImpl();
		purchaseVO = service.updatePurchase(purchaseVO);
		
		request.setAttribute("purchaseVO", purchaseVO);
		
		System.out.println("[UpdatePurchaseAction execute() end...]");
		return "forward:/purchase/updatePurchase.jsp";
	}

}
