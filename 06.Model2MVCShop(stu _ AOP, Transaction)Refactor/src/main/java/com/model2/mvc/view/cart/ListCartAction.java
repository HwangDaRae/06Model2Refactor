package com.model2.mvc.view.cart;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class ListCartAction extends Action {
	public ListCartAction() {
		System.out.println("[ListCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[ListCartAction execute() start...]");
		//left.jsp ���̾ �ִ� ��ٱ��� <a href Ŭ���� ������ �´� ��ٱ��� ����Ʈ�� �̵�

		User user = (User)request.getSession(true).getAttribute("user");
		if(user == null) {
			System.out.println("����� user == null");
			user = new User();
			user.setUserId("non-member");
			request.getSession(true).setAttribute("user", user);
			
			Cookie[] cookies = request.getCookies();
			
			//��Ű���� ��ǰ��ȣ ���� list ���
			ProductService p_service = new ProductServiceImpl();
			
			List<Product> proList = new ArrayList<Product>();
			String[] prodNoCookieValueArray = null;
			
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					System.out.println(cookies[i].getName());
					if(cookies[i].getName().equals("prodInfoCookie")) {
						System.out.println("��ȣ : " + URLDecoder.decode(cookies[i].getValue()));

						//��ǰ��ȣ�� ������ �´� ��ǰ������ �����´�
						prodNoCookieValueArray = URLDecoder.decode(cookies[i].getValue()).split(",");
						//10001:1
						//10002:3
						//10003:12
						String[] prodNoArray = null;
						for (int j = 0; j < prodNoCookieValueArray.length; j++) {
							prodNoArray = prodNoCookieValueArray[j].split(":");
							//10001
							//1
							//for (int j2 = 0; j2 < prodNoArray.length; j2++) {
								System.out.println("��Ű���� ���� ��ǰ��ȣ : " + prodNoArray[0]);
								System.out.println("�Ķ���ͷ� ���� ��ǰ���� : " + prodNoArray[1]);
								Product p = p_service.getProduct(Integer.parseInt(prodNoArray[0]));
								p.setAmount(Integer.parseInt(prodNoArray[1]));
								proList.add(p);
							//}
						}
					}
				}
			}
			
			for (int i = 0; i < proList.size(); i++) {
				System.out.println("proList : " + proList.get(i).toString());
			}
			
			request.setAttribute("list", proList);
			request.setAttribute("count", proList.size());
		}else if(user.getUserId().equals("non-member")) {
			System.out.println("����� user == non-member");
			user = new User();
			user.setUserId("non-member");
			request.getSession(true).setAttribute("user", user);
			
			Cookie[] cookies = request.getCookies();
			
			//��Ű���� ��ǰ��ȣ ���� list ���
			ProductService p_service = new ProductServiceImpl();
			
			List<Product> proList = new ArrayList<Product>();
			String[] prodNoCookieValueArray = null;
			
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					System.out.println(cookies[i].getName());
					if(cookies[i].getName().equals("prodInfoCookie")) {
						System.out.println("��ȣ : " + URLDecoder.decode(cookies[i].getValue()));

						//��ǰ��ȣ�� ������ �´� ��ǰ������ �����´�
						prodNoCookieValueArray = URLDecoder.decode(cookies[i].getValue()).split(",");
						//10001:1
						//10002:3
						//10003:12
						String[] prodNoArray = null;
						for (int j = 0; j < prodNoCookieValueArray.length; j++) {
							prodNoArray = prodNoCookieValueArray[j].split(":");
							//10001
							//1
							//for (int j2 = 0; j2 < prodNoArray.length; j2++) {
								System.out.println("��Ű���� ���� ��ǰ��ȣ : " + prodNoArray[0]);
								System.out.println("�Ķ���ͷ� ���� ��ǰ���� : " + prodNoArray[1]);
								Product p = p_service.getProduct(Integer.parseInt(prodNoArray[0]));
								p.setAmount(Integer.parseInt(prodNoArray[1]));
								proList.add(p);
							//}
						}
					}
				}
			}
			
			for (int i = 0; i < proList.size(); i++) {
				System.out.println("proList : " + proList.get(i).toString());
			}
			
			request.setAttribute("list", proList);
			request.setAttribute("count", proList.size());
		}else {
			CartService service = new CartServiceImpl();
			Map<String, Object> map = service.getCartList(user.getUserId());
			
			ArrayList<Cart> list = (ArrayList<Cart>)map.get("list");
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).toString());
			}
			
			request.setAttribute("list", map.get("list"));
			//count : �Խù� ��, listCart.jsp���� count>0�϶� for������ list���
			request.setAttribute("count", map.get("count") );
		}
		
		System.out.println("[ListCartAction execute() end...]");
		return "forward:/cart/listCart.jsp";
	}

}
