package com.model2.mvc.view.cart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;
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
		
		//���� ���������� ���� ��� üũ�ڽ��� üũ�� üũ�ڽ��� ���ؼ� index�� �˾Ƴ��� �� index�� �´� ��ǰ��ȣ, ������ ������
		
		//��ٱ��Ͽ��� ����
		String[] allCheckBoxProdNo = request.getParameterValues("addPurchaseCheckBox");
		int[] allCheckBoxProdNoNum = new int[allCheckBoxProdNo.length];
		for (int i = 0; i < allCheckBoxProdNo.length; i++) {
			allCheckBoxProdNoNum[i] = Integer.parseInt(allCheckBoxProdNo[i]);
		}
		
		//üũ�� ��ǰ��ȣ
		String[] checkedProdNo = request.getParameterValues("deleteCheckBox");
		//üũ�� ��ǰ�� ����
		String[] allamountProdNo = request.getParameterValues("amount");

		for (int i = 0; i < allCheckBoxProdNo.length; i++) {
			System.out.println("����� for����");
			for (int j = 0; j < checkedProdNo.length; j++) {
				System.out.println("����� for����2");
				if(checkedProdNo[j].equals(allCheckBoxProdNo[i])) {
					purchase = new Purchase();
					System.out.println("��ǰ��ȣ : " + allCheckBoxProdNo[i]);
					System.out.println("���� : " + allamountProdNo[i]);
					
					//������ ��ǰ����
					productVO = pService.getProduct(Integer.parseInt(allCheckBoxProdNo[i]));
					purchase.setPurchaseProd(productVO);
					
					//������ ��������
					User userVO = uService.getUser( ((User)request.getSession(true).getAttribute("user")).getUserId() );
					purchase.setBuyer(userVO);
					
					//������ ��ǰ�� ��������
					purchase.setAmount(Integer.parseInt(allamountProdNo[i]));
					
					purList.add(purchase);
				}
			}
		}
		request.setAttribute("purList", purList);
		
		System.out.println("[DeliveryCartAction execute() end...]");
		return "forward:/cart/deliveryCart.jsp";
	}

}
