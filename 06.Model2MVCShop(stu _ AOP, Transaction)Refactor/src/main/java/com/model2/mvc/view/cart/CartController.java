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
		// getProduct.jsp에서 장바구니 버튼 클릭시 장바구니에 추가된다.
		Cart cart = new Cart(prod_no, ((User)session.getAttribute("user")).getUserId(), product.getFileName(), product.getProdName(),
				product.getProdDetail(), amount, product.getPrice(), product.getAmount());		
		System.out.println("AddCartAction cart : " + cart.toString());
		
		if(((User)session.getAttribute("user")).getUserId().equals("non-member")) {
			//비회원 : 쿠키에 넣은 상품번호, 수량 가져온다
			Cookie[] cookies = request.getCookies();
			
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("prodInfoCookie")) {
						
						// 같은 상품번호 있다 => 수량 plus, 같은 상품번호 없다 => 수량 추가
						// 10001:12,10007:5,10013:31
						if(URLDecoder.decode(cookies[i].getValue()).indexOf(prod_no) != -1) {
							
							String value = URLDecoder.decode(cookies[i].getValue());
							System.out.println(value);
							
							int indexProdNoStart = value.indexOf(prod_no);
							String valueSubStr = value.substring(indexProdNoStart);
							int indexSeper = valueSubStr.indexOf(",");
							
							int cookieAmount = 0;
							if(indexSeper == -1) {
								// -1일때
								cookieAmount = Integer.parseInt(value.substring(indexProdNoStart+6));
								value.replace(prod_no+":"+cookieAmount, prod_no+":"+(cookieAmount+amount));
								System.out.println(value);
							}else {
								// 숫자일때
								int indexAmountStart = Integer.parseInt(value.substring(indexProdNoStart+6));
								String amountSubStr = value.substring(indexAmountStart);
								int indexSeperSub = amountSubStr.indexOf(",");
								cookieAmount = Integer.parseInt(value.substring(indexProdNoStart+6, indexSeperSub));
								value.replace(prod_no+":"+cookieAmount, prod_no+":"+(cookieAmount+amount));
								System.out.println(value);
							}
							
							// 장바구니 상품번호 같은게 있다 => 수량만 변경
							cart.setAmount(cookieAmount+amount);
							cartServiceImpl.updateAmount(cart);
							int returnIndex = value.lastIndexOf(",");
							
							Cookie cookie = new Cookie("prodInfoCookie", URLEncoder.encode(value.substring(0, returnIndex)));
							cookie.setMaxAge(24*60*60);
							response.addCookie(cookie);
							break;
						}
						
						// 장바구니에 추가
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
				}//end of cookieValueArr 장바구니에서 뿌려줄 list 가져오기
				
			}
			model.addAttribute("list", list);
			model.addAttribute("count", list.size());
		}else {
			// 회원 : 같은 상품이 있는지 비교할 리스트
			List<Cart> cartList = cartServiceImpl.getCartList( ((User)session.getAttribute("user")).getUserId() );
			
			//장바구니 전부를 가져와서 상품번호가 같다면 수량추가
			boolean isProdNo = false;
			for (int i = 0; i < cartList.size(); i++) {
				if(cartList.get(i).getProd_no() == prod_no){
					isProdNo = true;
					//장바구니에 상품이 있다면 => 수량 업데이트
					cart.setAmount(cartList.get(i).getAmount() + amount);
					cartServiceImpl.updateAmount(cart);
				}
			}
			
			if(!isProdNo) {
				//장바구니에 상품이 없다면 => insert
				cartServiceImpl.insertCart(cart);
			}
			//jsp에서 출력할 list 장바구니 list 가져온다
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
		//allAmount => 모든 수량, allCheckProdNo => 모든 상품번호, checkedProdNo => 체크된 상품번호
		//구매 페이지에서 받은 모든 체크박스와 체크된 체크박스를 비교해서 index를 알아낸다 그 index에 맞는 상품번호, 수량을 보낸다
		List<Purchase> purList = new ArrayList<Purchase>();
		Product productVO = new Product();

		for (int i = 0; i < allProdNo.length; i++) {
			for (int j = 0; j < checkProdNo.length; j++) {
				if(checkProdNo[j] == allProdNo[i]) {
					Purchase purchaseVO = new Purchase();
					System.out.println("상품번호 : " + checkProdNo[i]);
					System.out.println("수량 : " + allAmount[i]);
					
					//구매할 상품정보
					productVO = productServiceImpl.getProduct(checkProdNo[i]);
					purchaseVO.setPurchaseProd(productVO);
					
					//구매한 유저정보
					user.setUserId( ((User)session.getAttribute("user")).getUserId() );
					purchaseVO.setBuyer(user);
					
					//구매한 상품의 수량정보
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
		
		//1개 or 여러개 삭제시
		for (int i = 0; i < deleteArr.length; i++) {
			System.out.println("삭제할 상품 번호 : " + deleteArr[i]);
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
			map.put("deleteArray", deleteArr);
			map.put("user_id", ( (User)session.getAttribute("user") ).getUserId() );

			//장바구니에서 상품을 삭제하고 삭제한 list를 가져온다
			cartServiceImpl.deleteCart(map);
			List<Cart> list = cartServiceImpl.getCartList( ( (User)session.getAttribute("user") ).getUserId() );
			
			model.addAttribute("list", list);
			//count : 게시물 수, listCart.jsp에서 count>0일때 for문으로 list출력
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
		//left.jsp 레이어에 있는 장바구니 <a href 클릭시 유저에 맞는 장바구니 리스트로 이동
		User user = (User)session.getAttribute("user");
		System.out.println(user.getUserId());
		
		if(user.getUserId().equals("non-member")) {
			System.out.println("여기는 user.getUserId == non-member");
			
			Cookie[] cookies = request.getCookies();
			
			List<Product> proList = new ArrayList<Product>();
			String[] prodNoAndAmount = null;
			int[] prodNoArr;
			int[] amountArr;
			int index = 0;
			
			if(cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("prodInfoCookie")) {
						//상품번호와 수량에 맞는 상품정보를 가져온다
						index = i;
					}
				}

				System.out.println("index : " + index);
				System.out.println("prodInfoCookie로 찾은 cookie value : " + URLDecoder.decode(cookies[index].getValue()));
				prodNoAndAmount = URLDecoder.decode(cookies[index].getValue()).split(",");
				//10001:1
				//10022:31
				//10013:12
				
				for (int i = 0; i < prodNoAndAmount.length; i++) {
					System.out.println("파싱한 상품번호 and 수량 : " + prodNoAndAmount[i]);
				}
			}
			
			for (int i = 0; i < proList.size(); i++) {
				System.out.println("proList : " + proList.get(i).toString());
			}
			
			model.addAttribute("list", proList);
			model.addAttribute("count", proList.size());
		}else {
			List<Cart> list = new ArrayList<Cart>();
			System.out.println("여기는 회원 장바구니 리스트");
			list = cartServiceImpl.getCartList(user.getUserId());
			
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).toString());
			}
			
			model.addAttribute("list", list);
			//count : 게시물 수, listCart.jsp에서 count>0일때 for문으로 list출력
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

		// getProduct.jsp에서 장바구니 버튼 클릭시 장바구니에 추가된다.
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
			//비회원 : 쿠키에 넣은 상품번호, 수량 가져온다
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
						//상품번호에 맞는 상품정보를 가져온다
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
			// 회원 : 같은 상품이 있는지 비교하는 리스트
			CartService service = new CartServiceImpl();
			List<Cart> cartList = service.getCartList( user.getUserId() );
			
			//장바구니 전부를 가져와서 상품번호가 같다면 수량추가
			boolean isProdNo = false;
			ArrayList<Cart> p_list = (ArrayList<Cart>)map.get("list");
			for (int i = 0; i < p_list.size(); i++) {
				if(p_list.get(i).getProd_no() == Integer.parseInt(request.getParameter("prod_no"))){
					isProdNo = true;
					//수량 업데이트
					cart.setAmount(p_list.get(i).getAmount() + Integer.parseInt(request.getParameter("amount")));
					service.updateAmount(cart);
					System.out.println(cart.toString());
				}
			}
			
			System.out.println(!isProdNo);
			if(!isProdNo) {
				//상품번호가 다르다면 insert
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
