package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;

public class ListSaleAction extends Action {

	public ListSaleAction() {
		System.out.println("[ListSaleAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[ListSaleAction execute() start...]");
		System.out.println("[ListSaleAction execute() end...]");
		return null;
	}

}
