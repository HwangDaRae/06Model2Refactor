package com.model2.mvc.view.product;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.CommonUtil;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;

public class ListProductAction extends Action {

	public ListProductAction() {
		System.out.println("[ListProductAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[ListProductAction execute() start...]");
		
		// 비회원인지 회원인지 구분
		if((User)request.getSession(true).getAttribute("user") == null) {
			User user = new User();
			user.setUserId("non-member");
			request.getSession(true).setAttribute("user", user);
		}
		System.out.println("user_id : " + ( (User)request.getSession(true).getAttribute("user") ).getUserId());
		
		// admin 계정일때 판매상품관리와 상품검색을 구분해서 상품정보를 수정할지 조회할지 구분
		String menu = request.getParameter("menu");

		// 상품검색 클릭했을때 currentPage는 null이다
		int currentPage = 1;

		// 판매상품관리 클릭시 searchKeyword, searchCondition 둘 다 null
		String searchKeyword = CommonUtil.null2str(request.getParameter("searchKeyword"));
		String searchCondition = CommonUtil.null2str(request.getParameter("searchCondition"));

		// 상품검색 클릭시 null, 검색버튼 클릭시 nullString
		if (request.getParameter("currentPage") != null && !request.getParameter("currentPage").equals("")) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}

		// 검색정보 페이지 수, 컨디션, 키워드, pageUnit reqeust로 가져온 정보를 searchVO에 넣는다
		Search searchVO = new Search();
		searchVO.setCurruntPage(currentPage);

		// 상품명과 상품가격에서 searchKeyword가 문자일때 nullString으로 변환
		if (!searchCondition.trim().equals("1") && !CommonUtil.parsingCheck(searchKeyword)) {
			searchKeyword = "";
		}
		searchVO.setSearchCondition(searchCondition);
		searchVO.setSearchKeyword(searchKeyword);

		// page의 세로 사이즈와 가로 사이즈를 web.xml에서 가져와 넣는다
		int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSize"));
		int pageUnit = Integer.parseInt(getServletContext().getInitParameter("pageUnit"));
		searchVO.setPageSize(pageSize);

		// 가격순으로 display
		String priceSort = CommonUtil.null2str(request.getParameter("priceSort"));
		searchVO.setPriceSort(priceSort);
		System.out.println("searchVO.toString() : " + searchVO.toString());

		// 검색정보를 넣어서 현재 페이지의 list를 가져온다
		ProductService service = new ProductServiceImpl();
		Map<String, Object> map = service.getProductList(searchVO);

		// rssultPage로 paging처리
		Page resultPage = new Page(currentPage, ((Integer) map.get("count")).intValue(), pageUnit, pageSize);
		System.out.println("resultPage.toString() : " + resultPage.toString());

		// 검색정보와 검색해서 받은 list를 담는다
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("searchVO", searchVO);
		request.setAttribute("list", map.get("list"));
		request.setAttribute("menu", menu);

		System.out.println("[ListProductAction execute() end...]");
		return "forward:/product/listProduct.jsp";
	}

}
