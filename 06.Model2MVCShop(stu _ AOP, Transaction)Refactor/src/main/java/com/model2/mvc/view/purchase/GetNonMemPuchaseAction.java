package com.model2.mvc.view.purchase;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class GetNonMemPuchaseAction extends Action {

	public GetNonMemPuchaseAction() {
		System.out.println("[GetNonMemPuchaseAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[GetNonMemPuchaseAction execute() start...]");
		
		//비회원 주문조회
		String tranId = request.getParameter("tranId");
		System.out.println("tranId : " + tranId);

		PurchaseService purservice = new PurchaseServiceImpl();
		List<Purchase> purList = purservice.getListPurchase(tranId);
		for (int i = 0; i < purList.size(); i++) {
			System.out.println("getPurchase.jsp : " + purList.get(i).toString());
		}
		
		List<Product> proList = new ArrayList<Product>();
		for (int i = 0; i < purList.size(); i++) {
			ProductService proservice = new ProductServiceImpl();
			Product vo = proservice.getProduct(purList.get(i).getPurchaseProd().getProdNo());
			
			proList.add(vo);
		}
		for (int i = 0; i < proList.size(); i++) {
			System.out.println("get : " + proList.get(i).toString());
		}
		
		request.setAttribute("purList", purList);
		request.setAttribute("proList", proList);
		
		System.out.println("[GetNonMemPuchaseAction execute() end...]");
		return "forward:/purchase/getPurchase.jsp";
	}

}
