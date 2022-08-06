package com.model2.mvc.view.purchase;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class ListPurchaseAction extends Action {

	public ListPurchaseAction() {
		System.out.println("[ListPurchaseAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[ListPurchaseAction execute() start...]");
		String menu = request.getParameter("menu");
		Search searVO = new Search();
		String userID = ( (User)request.getSession(true).getAttribute("user") ).getUserId();
		
		int currentPage = 1;
		if(request.getParameter("currentPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		
		searVO.setCurruntPage(currentPage);
		
		int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSize"));
		int pageUnit = Integer.parseInt(getServletContext().getInitParameter("pageUnit"));
		searVO.setPageSize(pageSize);
		
		PurchaseService service = new PurchaseServiceImpl();
		List<Purchase> purchaseList = service.getPurchaseList(searVO, userID);
		int count = service.totalCountPurchaseList(userID);
		
		Page resultPage = new Page(currentPage, count, pageUnit, pageSize);

		request.setAttribute("list", purchaseList);
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("searVO", searVO);
		request.setAttribute("menu", menu);
		
		System.out.println("[ListPurchaseAction execute() end...]");
		return "forward:/purchase/listPurchase.jsp";
	}

}
