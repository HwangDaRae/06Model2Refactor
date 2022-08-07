package com.model2.mvc.view.purchase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

@Controller
public class PurchaseController {
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	PurchaseService purchaseServiceImpl;
	
	@Autowired
	@Qualifier("productServiceImpl")
	ProductService productServiceImpl;
	
	@Autowired
	@Qualifier("cartServiceImpl")
	CartService cartServiceImpl;

	public PurchaseController() {
		System.out.println(getClass() + " default Constructor()]");
	}
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	// ����
	@RequestMapping("/listPurchase.do")
	public ModelAndView listPurchase(Search searchVO, HttpSession session, Model model ) throws Exception {
		System.out.println("/listPurchase.do");
		
		String userId = ((User)session.getAttribute("user")).getUserId();
		
		int currentPage = 1;
		if(searchVO.getCurrentPage() != 0) {
			currentPage = searchVO.getCurrentPage();
		}
		searchVO.setCurrentPage(currentPage);
		searchVO.setPageSize(pageSize);
		
		List<Purchase> purchaseList = purchaseServiceImpl.getPurchaseList(searchVO, userId);
		int count = purchaseServiceImpl.totalCountPurchaseList(userId);
		
		Page resultPage = new Page(currentPage, count, pageUnit, pageSize);
		
		model.addAttribute("list", purchaseList);
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("searVO", searchVO);
		model.addAttribute("userId", userId);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/listPurchase.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/addPurchaseView.do")
	public ModelAndView addPurchaseView(@RequestParam("prod_no") int prod_no, @RequestParam("amount") int amount, Model model) throws Exception {
		System.out.println("/addPurchaseView.do");
		
		model.addAttribute("productVO", productServiceImpl.getProduct(prod_no));
		model.addAttribute("amount", amount);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/addPurchaseView.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/addPurchase.do")
	public ModelAndView addPurchase(@ModelAttribute("purchaseVO") Purchase purchaseVO, @RequestParam("amount") int[] amount, @RequestParam("prodNo") int prodNo, @RequestParam("productNo") int productNo[], HttpSession session, Model model) throws Exception {
		System.out.println("/addPurchase.do");

		List<Purchase> list = new ArrayList<Purchase>();
		Product productVO = new Product();
		
		//�ֹ���ȣ�� ���� �ĺ����ִ� ��
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		String tranId = sdf1.format( Calendar.getInstance().getTime() ) + "";
		// 2022-08-03 16:32:16
        String charsToRemove = "- :";
        for (char c : charsToRemove.toCharArray()) {
        	tranId = tranId.replace(String.valueOf(c), "");
        }
        //20220803163216

		System.out.println("purchaseVO.toString() defore : " + purchaseVO.toString());
		
        if(prodNo != 0) {
        	// ���������⿡�� ����
			// �ֹ���ȣ �ֱ�
			purchaseVO.setTranId(tranId);
        	//���������� ��ǰ����
        	productVO = productServiceImpl.getProduct(prodNo);
        	purchaseVO.setPurchaseProd(productVO);        	
        	// ���������� ��������
			purchaseVO.setBuyer((User)session.getAttribute("user"));
			// ������ ��ǰ�� ��������
			purchaseVO.setAmount(amount[0]);			
			// ������ ��ǰ�� totalPrice
			purchaseVO.setTotalPrice(amount[0] * productVO.getPrice());

			// ��ǰ ���� -= ������ ����
			productVO.setAmount(productVO.getAmount() - amount[0]);
			productServiceImpl.updateProduct(productVO);

			// ����ǰ������ ���������� PurchaseVO�� �ִ´�
			purchaseVO = purchaseServiceImpl.addPurchase(purchaseVO, productVO);
			System.out.println("purchaseVO.toString() after : " + purchaseVO.toString());

			list.add(purchaseVO);

			model.addAttribute("list", list);
        }else {
			// ��ٱ��Ͽ��� ����
        	// �迭�� �� ��ǰ��ȣ�� ��ǰ���� ���
        	/*
			for (int i = 0; i < productNo.length; i++) {
				System.out.println("��ǰ��ȣ : " + productNo[i] + ", ��ǰ���� : " + amount[i]);
				System.out.println("�������� : " + purchaseVO);
				
				// �ֹ���ȣ �ֱ�
				purchaseVO.setTranId(tranId);
				// ��ǰ���� ��������
				System.out.println("000");
				productVO = productServiceImpl.getProduct(productNo[i]);
				System.out.println("111");
				purchaseVO.setPurchaseProd(productVO);
				// �������� ��������
				purchaseVO.getBuyer().setUserId(((User)session.getAttribute("user")).getUserId());
				// ������ ��ǰ�� ��������
				purchaseVO.setAmount(amount[i]);
				
				// ��ǰ���� = ��ǰ���� - ���ż���
				productVO.setAmount(productVO.getAmount() - amount[i]);
				System.out.println("222");
				productServiceImpl.updateProduct(productVO);	
				System.out.println("333");			

				// ����ǰ������ ���������� PurchaseVO�� �ִ´�
				System.out.println("444");
				purchaseVO = purchaseServiceImpl.addPurchase(purchaseVO, productVO);
				System.out.println("555");
				System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

				list.add(purchaseVO);

				Map<String, Object> map = new HashMap<String, Object>();
				// ��ٱ��Ͽ��� ����
				map.put("user_id", ((User)session.getAttribute("user")).getUserId());
				map.put("deleteArray", productNo);
				System.out.println("666");
				cartServiceImpl.deleteCart(map);
				System.out.println("777");

				//@RequestParam ���� �� data�� �ִ� �������� �ٽ� ���� Call by Reference
				purchaseVO = new Purchase();
			}

			model.addAttribute("list", list);
    		for (Purchase purchase : list) {
    			System.out.println(purchase.toString());
    		}
			*/
        }
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/addPurchase.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/getPurchaseFromTranId.do")
	public ModelAndView getPurchaseFromTranId(@RequestParam("tranId") String tranId, Model model) throws Exception {
		System.out.println("/getPurchaseFromTranId.do");
		
		List<Purchase> purList = (List<Purchase>)purchaseServiceImpl.getPurchaseFromTranId(tranId);

		List<Product> proList = new ArrayList<Product>();
		for (int i = 0; i < purList.size(); i++) {
			Product product = new Product();
			product = productServiceImpl.getProduct(purList.get(i).getPurchaseProd().getProdNo());
			proList.add(product);
		}
		
		model.addAttribute("purList", purList);
		model.addAttribute("proList", proList);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/updatePurchaseView.do")
	public ModelAndView updatePurchaseView(@RequestParam("tranNo") int tranNo, Model model) throws Exception {
		System.out.println("/updatePurchaseView.do");
		
		Purchase purchaseVO = purchaseServiceImpl.getPurchase(tranNo);
		Product productVO = productServiceImpl.getProduct(purchaseVO.getPurchaseProd().getProdNo());
		
		model.addAttribute("purchaseVO", purchaseVO);
		model.addAttribute("productVO", productVO);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchaseView.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/updatePurchase.do")
	public ModelAndView updatePurchase(@ModelAttribute("purchaseVO") Purchase purchaseVO, Model model) throws Exception {
		System.out.println("/updatePurchase.do");
		
		purchaseVO = purchaseServiceImpl.updatePurchase(purchaseVO);
		purchaseVO.setTotalPrice(purchaseVO.getAmount() * purchaseVO.getPurchaseProd().getPrice());
		
		model.addAttribute("purchaseVO", purchaseVO);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchase.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/updateTranCode.do")
	public ModelAndView updateTranCode(@RequestParam("tranNo") int tranNo, @RequestParam("tranCode") String tranCode, HttpSession session, Model model, Search searchVO) throws Exception {
		System.out.println("/updateTranCode.do");
		//user���� : ����� => ��ۿϷ�
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tranNo", tranNo);
		map.put("prodNo", 0);
		map.put("tranCode", tranCode);
		
		purchaseServiceImpl.updateTranCode(map);

		//list�� ���ؼ�
		int currentPage = 1;
		if(searchVO.getCurrentPage() != 0) {
			currentPage = searchVO.getCurrentPage();
		}
		searchVO.setCurrentPage(currentPage);
		searchVO.setPageSize(pageSize);
		
		String userId = ( (User)session.getAttribute("user") ).getUserId();
		List<Purchase> purchaseList = purchaseServiceImpl.getPurchaseList(searchVO, userId);		
		Page resultPage = new Page(currentPage, purchaseServiceImpl.totalCountPurchaseList(userId), pageUnit, pageSize);
		
		model.addAttribute("list", purchaseList);
		model.addAttribute("searVO", searchVO);
		model.addAttribute("resultPage", resultPage);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/listPurchase.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/updateTranCodeByProd.do")
	public ModelAndView updateTranCodeByProd(@ModelAttribute("productVO") Product productVO, @ModelAttribute("purchaseVO") Purchase purchaseVO, Search searchVO, @RequestParam("prodNo") int prodNo, @RequestParam("tranCode") String tranCode, @RequestParam("menu") String menu, Model model) throws Exception {
		System.out.println("/updateTranCodeByProd.do");
		// admin���� : ���ſϷ� => �����
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tranNo", 0);
		map.put("prodNo", prodNo);
		map.put("tranCode", tranCode);
		
		purchaseServiceImpl.updateTranCode(map);
		
		//list�� ���ؼ�		
		int currentPage = 1;
		if(searchVO.getCurrentPage() != 0) {
			currentPage = searchVO.getCurrentPage();
		}
		searchVO.setCurrentPage(currentPage);
		searchVO.setPageSize(pageSize);

		List<Product> prodList = productServiceImpl.getProductList(searchVO);		
		Page resultPage = new Page(currentPage, productServiceImpl.getProductTotalCount(searchVO), pageUnit, pageSize);
		
		model.addAttribute("list", prodList);
		model.addAttribute("searchVO", searchVO);
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("menu", menu);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/product/listProduct.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}
	
	@RequestMapping("/getNonMemPurchase.do")
	public ModelAndView getNonMemPurchase(@RequestParam("tranId") String tranId, Model model) throws Exception {
		System.out.println("/getNonMemPurchase.do");
		
		//��ȸ�� �ֹ���ȸ
		List<Purchase> purList = purchaseServiceImpl.getListPurchase(tranId);		
		List<Product> proList = new ArrayList<Product>();
		for (int i = 0; i < purList.size(); i++) {
			Product vo = productServiceImpl.getProduct(purList.get(i).getPurchaseProd().getProdNo());
			proList.add(vo);
		}
		
		model.addAttribute("purList", purList);
		model.addAttribute("proList", proList);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("model", model);
		return modelAndView;
	}

}
