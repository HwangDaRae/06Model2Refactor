package com.model2.mvc.view.cart;

import java.net.URLDecoder;
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
import com.model2.mvc.service.domain.User;

public class DeleteCartAction extends Action {
	public DeleteCartAction() {
		System.out.println("[DeleteCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[DeleteCartAction execute() start...]");

		User user = (User)request.getSession(true).getAttribute("user");
		
		//1개 or 여러개 삭제시
		String[] delete = request.getParameterValues("deleteCheckBox");
		int[] deleteArray = new int[delete.length];
		for (int i=0; i<deleteArray.length; i++) {
			deleteArray[i] = Integer.parseInt(delete[i]);
			System.out.println("삭제할 상품번호 : " + deleteArray[i]);
		}
		
		if(user == null || user.getUserId().equals("non-member")) {
			// 비회원이라면
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
			System.out.println("return된 index : " + testIndex);
			
			// , 전까지 자른다 , 가 없다면 index부터 끝까지 자른다
			
			//request.setAttribute("list", map.get("list"));
			//count : 게시물 수, listCart.jsp에서 count>0일때 for문으로 list출력
			//request.setAttribute("count", map.get("count"));
		}else {
			//회원이라면
			Map<String, Object> map = new HashMap<String, Object>();
			
			//삭제할 상품번호와 user_id를 map에 넣는다
			map.put("deleteArray", deleteArray);
			map.put("user_id", ( (User)request.getSession(true).getAttribute("user") ).getUserId() );

			//장바구니에서 상품을 삭제하고 삭제한 list를 가져온다
			CartService service = new CartServiceImpl();
			service.deleteCart(map);
			List<Cart> list = service.getCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
			
			request.setAttribute("list", list);
			//count : 게시물 수, listCart.jsp에서 count>0일때 for문으로 list출력
			request.setAttribute("count", list.size());
		}
		
		System.out.println("[DeleteCartAction execute() end...]");
		return "forward:/cart/listCart.jsp";
	}

}
