package com.model2.mvc.view.cart;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;

public class DeliveryCartAction extends Action {
	public DeliveryCartAction() {
		System.out.println("[DeliveryCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[DeliveryCartAction execute() start...]");
		ProductService pService = new ProductServiceImpl();
		UserService uService = new UserServiceImpl();
		List<Purchase> purList = new ArrayList<Purchase>();
		Product productVO = new Product();
		Purchase purchase = null;
		
		//구매 페이지에서 받은 모든 체크박스와 체크된 체크박스를 비교해서 index를 알아낸다 그 index에 맞는 상품번호, 수량을 보낸다
		
		//장바구니에서 구매
		String[] allCheckBoxProdNo = request.getParameterValues("addPurchaseCheckBox");
		int[] allCheckBoxProdNoNum = new int[allCheckBoxProdNo.length];
		for (int i = 0; i < allCheckBoxProdNo.length; i++) {
			allCheckBoxProdNoNum[i] = Integer.parseInt(allCheckBoxProdNo[i]);
		}
		
		//체크된 상품번호
		String[] checkedProdNo = request.getParameterValues("deleteCheckBox");
		//체크된 상품의 수량
		String[] allamountProdNo = request.getParameterValues("amount");

		for (int i = 0; i < allCheckBoxProdNo.length; i++) {
			System.out.println("여기는 for문안");
			for (int j = 0; j < checkedProdNo.length; j++) {
				System.out.println("여기는 for문안2");
				if(checkedProdNo[j].equals(allCheckBoxProdNo[i])) {
					purchase = new Purchase();
					System.out.println("상품번호 : " + allCheckBoxProdNo[i]);
					System.out.println("수량 : " + allamountProdNo[i]);
					
					//구매할 상품정보
					productVO = pService.getProduct(Integer.parseInt(allCheckBoxProdNo[i]));
					purchase.setPurchaseProd(productVO);
					
					//구매한 유저정보
					User userVO = uService.getUser( ((User)request.getSession(true).getAttribute("user")).getUserId() );
					purchase.setBuyer(userVO);
					
					//구매한 상품의 수량정보
					purchase.setAmount(Integer.parseInt(allamountProdNo[i]));
					
					purList.add(purchase);
				}
			}
		}
		request.setAttribute("purList", purList);
		request.setAttribute("count", purList.size());
		
		System.out.println("[DeliveryCartAction execute() end...]");
		return "forward:/cart/deliveryCart.jsp";
	}

}
