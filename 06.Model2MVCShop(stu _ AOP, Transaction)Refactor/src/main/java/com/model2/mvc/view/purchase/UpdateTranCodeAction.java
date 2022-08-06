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

public class UpdateTranCodeAction extends Action {

	public UpdateTranCodeAction() {
		System.out.println("[UpdateTranCodeAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[UpdateTranCodeAction execute() start...]");
		String menu = request.getParameter("menu");
		String tranCode = request.getParameter("tranCode");
		int tranNo = Integer.parseInt(request.getParameter("tranNo"));
		
		Purchase purchaseVO = new Purchase();
		purchaseVO.setTranCode(tranCode);
		purchaseVO.setTranNo(tranNo);
		
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
		
		String userID = ( (User)request.getSession(true).getAttribute("user") ).getUserId();
		List<Purchase> purchaseList = service.getPurchaseList(searchVO, userID);
		int count = service.totalCountPurchaseList(userID);
		
		Page resultPage = new Page(currentPage, count, pageUnit, pageSize);

		request.setAttribute("list", purchaseList);
		request.setAttribute("searVO", searchVO);
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("menu", menu);

		System.out.println("[UpdateTranCodeAction execute() end...]");
		return "forward:/purchase/listPurchase.jsp";
	}

}
