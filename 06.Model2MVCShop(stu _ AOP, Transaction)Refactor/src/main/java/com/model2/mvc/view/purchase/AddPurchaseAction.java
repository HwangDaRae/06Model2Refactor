package com.model2.mvc.view.purchase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;

public class AddPurchaseAction extends Action {

	public AddPurchaseAction() {
		System.out.println("[AddPurchaseAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[AddPurchaseAction execute() start...]");
		Map<String, Object> map = new HashMap<String, Object>();
		ProductService pService = new ProductServiceImpl();
		UserService uService = new UserServiceImpl();
		PurchaseService service = new PurchaseServiceImpl();
		Product productVO = new Product();
		CartService cService = new CartServiceImpl();
		ArrayList<Purchase> list = new ArrayList<Purchase>();

		Purchase purchaseVO = new Purchase();
		purchaseVO.setPaymentOption(request.getParameter("paymentOption")); // ���Ź��
		purchaseVO.setReceiverName(request.getParameter("receiverName")); // �������̸�
		purchaseVO.setReceiverPhone(request.getParameter("receiverPhone")); // �����ڿ���ó
		purchaseVO.setDivyAddr(request.getParameter("receiverAddr")); // �������ּ�
		purchaseVO.setDivyRequest(request.getParameter("receiverRequest")); // �����ڿ�û����
		purchaseVO.setDivyDate(request.getParameter("receiverDate")); // ����������

		System.out.println("���� : " + purchaseVO.toString());
		
		//�ֹ���ȣ�� ���� �ĺ����ִ� ��
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		String tranId = sdf1.format( Calendar.getInstance().getTime() ) + "";
		// 2022-08-03 16:32:16
        String charsToRemove = "- :";
 
        for (char c : charsToRemove.toCharArray()) {
        	tranId = tranId.replace(String.valueOf(c), "");
        }
        System.out.println(tranId);
        // 20220803163216
		

		if (request.getParameter("prodNo") != null) {
			// ���������� ����
			String prodNo = request.getParameter("prodNo");

			// ���������� ��ǰ����
			productVO = pService.getProduct(Integer.parseInt(prodNo));
			purchaseVO.setPurchaseProd(productVO);

			// ���������� ��������
			User userCheck = (User)request.getSession(true).getAttribute("user");
			if( userCheck == null | userCheck.getUserId().equals("non-member")) {
				User userVO = new User();
				userVO.setUserId("non-member");
				purchaseVO.setBuyer(userVO);
			}else {
				User userVO = uService.getUser(((User) request.getSession(true).getAttribute("user")).getUserId());
				purchaseVO.setBuyer(userVO);
			}

			// ������ ��ǰ�� ��������
			purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount"))); // ����

			// ��ǰ ���� -= ������ ����
			productVO.setAmount(productVO.getAmount() - Integer.parseInt(request.getParameter("amount")));
			pService.updateProduct(productVO);
			
			// ������ ��ǰ�� totalPrice
			purchaseVO.setTotalPrice(Integer.parseInt(request.getParameter("amount")) * productVO.getPrice());
			
			// �ֹ���ȣ �ֱ�
			purchaseVO.setTranId(tranId);

			// ����ǰ������ ���������� PurchaseVO�� �ִ´�
			purchaseVO = service.addPurchase(purchaseVO, productVO);
			System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

			list.add(purchaseVO);

			request.setAttribute("list", list);
		} else {
			// ��ٱ��Ͽ��� ����

			// �迭�� �� ��ǰ��ȣ�� ��ǰ���� ���
			String[] prodductNo = request.getParameterValues("productNo");
			String[] productAmount = request.getParameterValues("amount");

			for (int i = 0; i < prodductNo.length; i++) {
				System.out.println("��ǰ��ȣ : " + prodductNo[i] + ", ��ǰ���� : " + productAmount[i]);
				// ��ǰ���� ��������
				productVO = pService.getProduct(Integer.parseInt(prodductNo[i]));
				purchaseVO.setPurchaseProd(productVO);

				// �������� ��������
				User userVO = uService.getUser(((User) request.getSession(true).getAttribute("user")).getUserId());
				purchaseVO.setBuyer(userVO);

				// ������ ��ǰ�� ��������
				purchaseVO.setAmount(Integer.parseInt(productAmount[i])); // ����

				// ��ǰ���� = ��ǰ���� - ���ż���
				productVO.setAmount(productVO.getAmount() - Integer.parseInt(productAmount[i]));
				pService.updateProduct(productVO);
				
				// �ֹ���ȣ �ֱ�
				purchaseVO.setTranId(tranId);

				// ����ǰ������ ���������� PurchaseVO�� �ִ´�
				purchaseVO = service.addPurchase(purchaseVO, productVO);
				System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

				list.add(purchaseVO);

				// ��ٱ��Ͽ��� ����
				map.put("user_id", ((User)request.getSession(true).getAttribute("user")).getUserId());
				map.put("deleteArray", prodductNo);
				//cService.deleteCart(map);

				purchaseVO = new Purchase();
			}
		}
		request.setAttribute("list", list);
		for (Purchase purchase : list) {
			System.out.println(purchase.toString());
		}

		System.out.println("[AddPurchaseAction execute() end...]");
		return "forward:/purchase/addPurchase.jsp";
	}

}
