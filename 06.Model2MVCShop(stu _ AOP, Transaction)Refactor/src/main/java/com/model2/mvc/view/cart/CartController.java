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
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

@Controller
public class CartController {
	
	@Autowired
	@Qualifier("cartServiceImpl")
	CartService cartServiceImpl;
	
	@Autowired
	@Qualifier("productServiceImpl")
	ProductService productServiceImpl;
	
	public CartController() {
		System.out.println(getClass() + " default Constructor()]");
	}
	
	@RequestMapping("/addCart.do")
	public ModelAndView addCart(HttpServletRequest request, HttpServletResponse response, @RequestParam("prod_no") int prod_no, @RequestParam("amount") int amount, HttpSession session, Model model) throws Exception {
		System.out.println("/addCart.do");
		List<Product> list = new ArrayList<Product>();
		
		Product product = productServiceImpl.getProduct(prod_no);
		// getProduct.jsp���� ��ٱ��� ��ư Ŭ���� ��ٱ��Ͽ� �߰��ȴ�.
		Cart cart = new Cart(prod_no, ((User)session.getAttribute("user")).getUserId(), product.getFileName(), product.getProdName(),
				product.getProdDetail(), amount, product.getPrice(), product.getAmount());		
		System.out.println("AddCartAction cart : " + cart.toString());
		
		if(((User)session.getAttribute("user")).getUserId().equals("non-member")) {
			//��ȸ�� : ��Ű�� ���� ��ǰ��ȣ, ���� �����´�
			Cookie[] cookies = request.getCookies();
			
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("prodInfoCookie")) {
						
						// ���� ��ǰ��ȣ �ִ� => ���� plus, ���� ��ǰ��ȣ ���� => ���� �߰�
						// 10001:12,10007:5,10013:31
						if(URLDecoder.decode(cookies[i].getValue()).indexOf(prod_no) != -1) {
							
							String value = URLDecoder.decode(cookies[i].getValue());
							System.out.println(value);
							
							int indexProdNoStart = value.indexOf(prod_no);
							String valueSubStr = value.substring(indexProdNoStart);
							int indexSeper = valueSubStr.indexOf(",");
							
							int cookieAmount = 0;
							if(indexSeper == -1) {
								// -1�϶�
								cookieAmount = Integer.parseInt(value.substring(indexProdNoStart+6));
								value.replace(prod_no+":"+cookieAmount, prod_no+":"+(cookieAmount+amount));
								System.out.println(value);
							}else {
								// �����϶�
								int indexAmountStart = Integer.parseInt(value.substring(indexProdNoStart+6));
								String amountSubStr = value.substring(indexAmountStart);
								int indexSeperSub = amountSubStr.indexOf(",");
								cookieAmount = Integer.parseInt(value.substring(indexProdNoStart+6, indexSeperSub));
								value.replace(prod_no+":"+cookieAmount, prod_no+":"+(cookieAmount+amount));
								System.out.println(value);
							}
							
							// ��ٱ��� ��ǰ��ȣ ������ �ִ� => ������ ����
							cart.setAmount(cookieAmount+amount);
							cartServiceImpl.updateAmount(cart);
							int returnIndex = value.lastIndexOf(",");
							
							Cookie cookie = new Cookie("prodInfoCookie", URLEncoder.encode(value.substring(0, returnIndex)));
							cookie.setMaxAge(24*60*60);
							response.addCookie(cookie);
							break;
						}
						
						// ��ٱ��Ͽ� �߰�
						cartServiceImpl.insertCart(cart);
						
						Cookie cookie = new Cookie("prodInfoCookie", URLEncoder.encode(URLDecoder.decode(cookies[i].getValue()) + ":" + amount));
						cookie.setMaxAge(24*60*60);
						response.addCookie(cookie);
					}
				}

				String cookieValue = "";
				for (int x = 0; x < cookies.length; x++) {
					if(cookies[x].getName().equals("prodInfoCookie")) {
						cookieValue = URLDecoder.decode(cookies[x].getValue());
					}
				}
				
				String[] cookieValueArr = cookieValue.split(",");
				//10002:3
				//10031:12
				//10013:10
				String[] prodNoAndAmount = new String[cookieValueArr.length];
				for (int y = 0; y < cookieValueArr.length; y++) {
					Product p = new Product();
					prodNoAndAmount = cookieValueArr[y].split(":");
					//10002
					//3
					p = productServiceImpl.getProduct(Integer.parseInt(prodNoAndAmount[0]));
					p.setAmount(Integer.parseInt(prodNoAndAmount[1]));
					list.add(p);
				}//end of cookieValueArr ��ٱ��Ͽ��� �ѷ��� list ��������
				
			}
			model.addAttribute("list", list);
			model.addAttribute("count", list.size());
		}else {
			// ȸ�� : ���� ��ǰ�� �ִ��� ���� ����Ʈ
			List<Cart> cartList = cartServiceImpl.getCartList( ((User)session.getAttribute("user")).getUserId() );
			
			//��ٱ��� ���θ� �����ͼ� ��ǰ��ȣ�� ���ٸ� �����߰�
			boolean isProdNo = false;
			for (int i = 0; i < cartList.size(); i++) {
				if(cartList.get(i).getProd_no() == prod_no){
					isProdNo = true;
					//��ٱ��Ͽ� ��ǰ�� �ִٸ� => ���� ������Ʈ
					cart.setAmount(cartList.get(i).getAmount() + amount);
					cartServiceImpl.updateAmount(cart);
				}
			}
			
			if(!isProdNo) {
				//��ٱ��Ͽ� ��ǰ�� ���ٸ� => insert
				cartServiceImpl.insertCart(cart);
			}
			//jsp���� ����� list ��ٱ��� list �����´�
			cartList = cartServiceImpl.getCartList( ((User)session.getAttribute("user")).getUserId() );
			
			model.addAttribute("list", cartList);
			model.addAttribute("count", cartList.size());
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/cart/listCart.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}

	/*
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
	*/

}
