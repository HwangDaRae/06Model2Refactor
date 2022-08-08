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

import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
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
	
	@RequestMapping("/deliveryCart.do")
	public ModelAndView deliveryCart(@RequestParam("addPurchaseCheckBox") int[] allProdNo, @RequestParam("deleteCheckBox") int[] checkProdNo, @RequestParam("amount") int[] allAmount,
			HttpSession session, User user, Model model) throws Exception {
		System.out.println("/deliveryCart.do");
		//allAmount => ��� ����, allCheckProdNo => ��� ��ǰ��ȣ, checkedProdNo => üũ�� ��ǰ��ȣ
		//���� ���������� ���� ��� üũ�ڽ��� üũ�� üũ�ڽ��� ���ؼ� index�� �˾Ƴ��� �� index�� �´� ��ǰ��ȣ, ������ ������
		List<Purchase> purList = new ArrayList<Purchase>();
		Product productVO = new Product();

		for (int i = 0; i < allProdNo.length; i++) {
			for (int j = 0; j < checkProdNo.length; j++) {
				if(checkProdNo[j] == allProdNo[i]) {
					Purchase purchaseVO = new Purchase();
					System.out.println("��ǰ��ȣ : " + checkProdNo[i]);
					System.out.println("���� : " + allAmount[i]);
					
					//������ ��ǰ����
					productVO = productServiceImpl.getProduct(checkProdNo[i]);
					purchaseVO.setPurchaseProd(productVO);
					
					//������ ��������
					user.setUserId( ((User)session.getAttribute("user")).getUserId() );
					purchaseVO.setBuyer(user);
					
					//������ ��ǰ�� ��������
					purchaseVO.setAmount(allAmount[i]);
					
					purList.add(purchaseVO);
				}
			}
		}
		
		for (int i = 0; i < purList.size(); i++) {
			System.out.println("purList : " + purList.get(i));
		}
		
		model.addAttribute("purList", purList);
		model.addAttribute("count", purList.size());
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/cart/deliveryCart.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/deleteCart.do")
	public ModelAndView deleteCart( @RequestParam("deleteCheckBox") int[] deleteArr, HttpSession session, HttpServletRequest request, Model model) throws Exception {
		System.out.println("/deleteCart.do");

		User user = (User)session.getAttribute("user");
		
		//1�� or ������ ������
		for (int i = 0; i < deleteArr.length; i++) {
			System.out.println("������ ��ǰ ��ȣ : " + deleteArr[i]);
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
			map.put("deleteArray", deleteArr);
			map.put("user_id", ( (User)session.getAttribute("user") ).getUserId() );

			//��ٱ��Ͽ��� ��ǰ�� �����ϰ� ������ list�� �����´�
			cartServiceImpl.deleteCart(map);
			List<Cart> list = cartServiceImpl.getCartList( ( (User)session.getAttribute("user") ).getUserId() );
			
			model.addAttribute("list", list);
			//count : �Խù� ��, listCart.jsp���� count>0�϶� for������ list���
			model.addAttribute("count", list.size());
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/cart/listCart.jsp");
		modelAndView.addObject("model", model);
		
		return modelAndView;
	}
	
	@RequestMapping("/listCart.do")
	public ModelAndView listCart(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		System.out.println("/listCart.do");
		//left.jsp ���̾ �ִ� ��ٱ��� <a href Ŭ���� ������ �´� ��ٱ��� ����Ʈ�� �̵�
		User user = (User)session.getAttribute("user");
		System.out.println(user.getUserId());
		
		if(user.getUserId().equals("non-member")) {
			System.out.println("����� user.getUserId == non-member");
			
			Cookie[] cookies = request.getCookies();
			
			List<Product> proList = new ArrayList<Product>();
			String[] prodNoAndAmount = null;
			int[] prodNoArr;
			int[] amountArr;
			int index = 0;
			
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("prodInfoCookie")) {
						//��ǰ��ȣ�� ������ �´� ��ǰ������ �����´�
						index = i;
					}
				}

				System.out.println("index : " + index);
				System.out.println("prodInfoCookie�� ã�� cookie value : " + URLDecoder.decode(cookies[index].getValue()));
				prodNoAndAmount = URLDecoder.decode(cookies[index].getValue()).split(",");
				//10001:1
				//10022:31
				//10013:12
				
				for (int i = 0; i < prodNoAndAmount.length; i++) {
					System.out.println("�Ľ��� ��ǰ��ȣ and ���� : " + prodNoAndAmount[i]);
				}
			}
			
			for (int i = 0; i < proList.size(); i++) {
				System.out.println("proList : " + proList.get(i).toString());
			}
			
			model.addAttribute("list", proList);
			model.addAttribute("count", proList.size());
		}else {
			List<Cart> list = new ArrayList<Cart>();
			System.out.println("����� ȸ�� ��ٱ��� ����Ʈ");
			list = cartServiceImpl.getCartList(user.getUserId());
			
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).toString());
			}
			
			model.addAttribute("list", list);
			//count : �Խù� ��, listCart.jsp���� count>0�϶� for������ list���
			model.addAttribute("count", list.size());
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
