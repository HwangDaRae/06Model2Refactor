package com.model2.mvc.view.cart;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.User;

public class DeleteCartAction extends Action {
	public DeleteCartAction() {
		System.out.println("[DeleteCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[DeleteCartAction execute() start...]");

		User user = (User)request.getSession(true).getAttribute("user");
		
		//1�� or ������ ������
		String[] delete = request.getParameterValues("deleteCheckBox");
		int[] deleteArray = new int[delete.length];
		for (int i=0; i<deleteArray.length; i++) {
			deleteArray[i] = Integer.parseInt(delete[i]);
			System.out.println("������ ��ǰ��ȣ : " + deleteArray[i]);
		}
		
		if(user == null || user.getUserId().equals("non-member")) {
			// ��ȸ���̶��
			String allInfo = "";
			
			Cookie[] cookies = request.getCookies();
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("prodInfoCookie")) {
						allInfo = URLDecoder.decode(cookies[i].getValue());
					}
				}
			}
			
			String testStr = "10001:2,10003:5,10007:12";
			int testIndex = testStr.indexOf("10003");
			System.out.println("return�� index : " + testIndex);
			
			// , ������ �ڸ��� , �� ���ٸ� index���� ������ �ڸ���
			
			//request.setAttribute("list", map.get("list"));
			//count : �Խù� ��, listCart.jsp���� count>0�϶� for������ list���
			//request.setAttribute("count", map.get("count"));
		}else {
			//ȸ���̶��
			Map<String, Object> map = new HashMap<String, Object>();
			
			//������ ��ǰ��ȣ�� user_id�� map�� �ִ´�
			map.put("deleteArray", deleteArray);
			map.put("user_id", ( (User)request.getSession(true).getAttribute("user") ).getUserId() );

			//��ٱ��Ͽ��� ��ǰ�� �����ϰ� ������ list�� �����´�
			CartService service = new CartServiceImpl();
			service.deleteCart(map);
			map = service.getCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
			
			request.setAttribute("list", map.get("list"));
			//count : �Խù� ��, listCart.jsp���� count>0�϶� for������ list���
			request.setAttribute("count", map.get("count"));
		}
		
		System.out.println("[DeleteCartAction execute() end...]");
		return "forward:/cart/listCart.jsp";
	}

}
