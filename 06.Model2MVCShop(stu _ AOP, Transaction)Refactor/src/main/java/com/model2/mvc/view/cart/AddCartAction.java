package com.model2.mvc.view.cart;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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

public class AddCartAction extends Action {
	public AddCartAction() {
		System.out.println("[AddCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[AddCartAction execute() start...]");
		Map<String, Object> map = new HashMap<String, Object>();
		Cart cart = new Cart();
		
		ProductService p_service = new ProductServiceImpl();
		Product product = p_service.getProduct( Integer.parseInt(request.getParameter("prod_no")) );

		// getProduct.jsp���� ��ٱ��� ��ư Ŭ���� ��ٱ��Ͽ� �߰��ȴ�.
		cart.setProd_no(Integer.parseInt(request.getParameter("prod_no")));
		User user = (User)request.getSession(true).getAttribute("user");
		cart.setUser_id( user.getUserId() );
		cart.setImage(product.getFileName());
		cart.setProd_name(product.getProdName());
		cart.setProd_detail(product.getProdDetail());
		cart.setAmount(Integer.parseInt(request.getParameter("amount")));
		cart.setPrice(product.getPrice());
		
		System.out.println("AddCartAction cart : " + cart.toString());
		
		if(user == null || user.getUserId().equals("non-member")) {
			//��ȸ�� : ��Ű�� ���� ��ǰ��ȣ, ���� �����´�
			List<Product> proList = new ArrayList<Product>();
			
			Cookie[] cookies = request.getCookies();
			
			String[] prodNoCookieValueArray = null;
			String prodNoCookieValue = "";
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("prodInfoCookie")) {
						Cookie cookie = new Cookie("prodInfoCookie", URLEncoder.encode(URLDecoder.decode(cookies[i].getValue()) + ":" + request.getParameter("amount")) );
						cookie.setMaxAge(24*60*60);
						response.addCookie(cookie);
						//��ǰ��ȣ�� �´� ��ǰ������ �����´�
						prodNoCookieValueArray = URLDecoder.decode(cookies[i].getValue()).split(",");
						String[] prodNoArray = null;
						Product p = null;
						for (int j = 0; j < prodNoCookieValueArray.length; j++) {
							prodNoArray = prodNoCookieValueArray[j].split(":");
							p = p_service.getProduct(Integer.parseInt(prodNoArray[0]));
							p.setAmount(Integer.parseInt(request.getParameter("amount")));
							proList.add(p);
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
			// ȸ�� : ���� ��ǰ�� �ִ��� ���ϴ� ����Ʈ
			CartService service = new CartServiceImpl();
			List<Cart> cartList = service.getCartList( user.getUserId() );
			
			//��ٱ��� ���θ� �����ͼ� ��ǰ��ȣ�� ���ٸ� �����߰�
			boolean isProdNo = false;
			ArrayList<Cart> p_list = (ArrayList<Cart>)map.get("list");
			for (int i = 0; i < p_list.size(); i++) {
				if(p_list.get(i).getProd_no() == Integer.parseInt(request.getParameter("prod_no"))){
					isProdNo = true;
					//���� ������Ʈ
					cart.setAmount(p_list.get(i).getAmount() + Integer.parseInt(request.getParameter("amount")));
					service.updateAmount(cart);
					System.out.println(cart.toString());
				}
			}
			
			System.out.println(!isProdNo);
			if(!isProdNo) {
				//��ǰ��ȣ�� �ٸ��ٸ� insert
				service.insertCart(cart);
			}
			 cartList = service.getCartList( user.getUserId() );
			
			request.setAttribute("list", cartList);
			request.setAttribute("count", cartList.size());
		}
		

		System.out.println("[AddCartAction execute() end...]");
		return "forward:/cart/listCart.jsp";
	}

}
