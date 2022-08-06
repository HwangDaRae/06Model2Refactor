package com.model2.mvc.view.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class UpdateTranCodeByProdAction extends Action {

	public UpdateTranCodeByProdAction() {
		System.out.println("[UpdateTranCodeByProdAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[UpdateTranCodeByProdAction execute() start...]");
		String menu = request.getParameter("menu");
		int prodNo = Integer.parseInt(request.getParameter("prodNo"));
		String tanscode = request.getParameter("tanscode");
		System.out.println("prodNo : " + prodNo + ", tanscode : " + tanscode);
		
		Purchase purchaseVO = new Purchase();
		Product productVO = new Product();
		productVO.setProdNo(prodNo);
		purchaseVO.setPurchaseProd(productVO);
		purchaseVO.setTranCode(tanscode);
		System.out.println("purchaseVO : " + purchaseVO);
		
		PurchaseService service = new PurchaseServiceImpl();
		service.updateTranCode(purchaseVO);

		//list를 위해서
		Search searchVO = new Search();
		
		int currentPage = 1;
		if(request.getParameter("currentPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		int pageSize = Integer.parseInt( getServletContext().getInitParameter("pageSize") );
		int pageUnit = Integer.parseInt( getServletContext().getInitParameter("pageUnit") );
		searchVO.setCurruntPage(currentPage);
		searchVO.setPageSize(pageSize);
		
		ProductService pservice = new ProductServiceImpl();
		Map<String, Object> map = pservice.getProductList(searchVO);
		
		Page resultPage = new Page(currentPage, ((Integer)map.get("count")).intValue(), pageUnit, pageSize);

		request.setAttribute("list", map.get("list"));
		request.setAttribute("searchVO", searchVO);
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("menu", menu);
		
		System.out.println("[UpdateTranCodeByProdAction execute() end...]");
		return "forward:/product/listProduct.jsp";
	}

}
