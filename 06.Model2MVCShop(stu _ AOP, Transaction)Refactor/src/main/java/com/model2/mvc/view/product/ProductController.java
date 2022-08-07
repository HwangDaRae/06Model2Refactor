package com.model2.mvc.view.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.CommonUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;

@Controller
public class ProductController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	ProductService productServiceImpl;
	

	public ProductController() {
		System.out.println(getClass() + " default Constructor()]");
	}
	
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping("/listProduct.do")
	public String listProduct(Model model, User user, HttpSession session, HttpServletRequest request, HttpServletResponse response, String menu, Search search) throws Exception {
		System.out.println("/listProduct.do");
		System.out.println(search);
		System.out.println(user);
		System.out.println(menu);
		System.out.println(session.getAttribute("user"));
		
		if(((User)session.getAttribute("user")).getUserId().equals("non-member")) {
			//��ȸ�� ��ǰ �˻� Anchor Tag Ŭ��
			System.out.println("��ȸ������ ���Դ�");
		}else if(((User)session.getAttribute("user")).getRole().equals("admin")) {
			System.out.println("admin�������� ���Դ�");
		}else {
			System.out.println("user�������� ���Դ�");
		}

		// ��ǰ�˻� Ŭ�������� currentPage�� null�̴�
		int currentPage = 1;

		// ��ǰ�˻� Ŭ���� null, �˻���ư Ŭ���� nullString
		if (search.getCurrentPage() != 0) {
			currentPage = search.getCurrentPage();
		}

		// �ǸŻ�ǰ���� Ŭ���� searchKeyword, searchCondition �� �� null ==> nullString ���� ��ȯ
		String searchKeyword = CommonUtil.null2str(search.getSearchKeyword());
		String searchCondition = CommonUtil.null2str(search.getSearchCondition());
		
		// ��ǰ��� ��ǰ���ݿ��� searchKeyword�� �����϶� nullString���� ��ȯ
		if (!searchCondition.trim().equals("1") && !CommonUtil.parsingCheck(searchKeyword)) {
			searchKeyword = "";
		}
		search = new Search(currentPage, searchCondition, searchKeyword, pageSize, search.getPriceSort());
		
		// �˻������� �־ ���� �������� list�� �����´�
		List<Product> prodList = productServiceImpl.getProductList(search);		
		int totalCount = productServiceImpl.getProductTotalCount(search);		
		Page resultPage = new Page(currentPage, totalCount, pageUnit, pageSize);
		
		for (int i = 0; i < prodList.size(); i++) {
			System.out.println(getClass() + " : " + prodList.get(i).toString());
		}

		request.setAttribute("resultPage", resultPage);
		request.setAttribute("searchVO", search);
		request.setAttribute("list", prodList);
		request.setAttribute("listSize", prodList.size());
		request.setAttribute("menu", menu);
		
		return "forward:/product/listProduct.jsp";
	}
	
	

	/*
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[ListProductAction execute() start...]");
		
		// ��ȸ������ ȸ������ ����
		if((User)request.getSession(true).getAttribute("user") == null) {
			User user = new User();
			user.setUserId("non-member");
			request.getSession(true).setAttribute("user", user);
		}
		System.out.println("user_id : " + ( (User)request.getSession(true).getAttribute("user") ).getUserId());
		
		// admin �����϶� �ǸŻ�ǰ������ ��ǰ�˻��� �����ؼ� ��ǰ������ �������� ��ȸ���� ����
		String menu = request.getParameter("menu");

		// ��ǰ�˻� Ŭ�������� currentPage�� null�̴�
		int currentPage = 1;

		// �ǸŻ�ǰ���� Ŭ���� searchKeyword, searchCondition �� �� null
		String searchKeyword = CommonUtil.null2str(request.getParameter("searchKeyword"));
		String searchCondition = CommonUtil.null2str(request.getParameter("searchCondition"));

		// ��ǰ�˻� Ŭ���� null, �˻���ư Ŭ���� nullString
		if (request.getParameter("currentPage") != null && !request.getParameter("currentPage").equals("")) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}

		// �˻����� ������ ��, �����, Ű����, pageUnit reqeust�� ������ ������ searchVO�� �ִ´�
		Search searchVO = new Search();
		searchVO.setCurruntPage(currentPage);

		// ��ǰ��� ��ǰ���ݿ��� searchKeyword�� �����϶� nullString���� ��ȯ
		if (!searchCondition.trim().equals("1") && !CommonUtil.parsingCheck(searchKeyword)) {
			searchKeyword = "";
		}
		searchVO.setSearchCondition(searchCondition);
		searchVO.setSearchKeyword(searchKeyword);

		// page�� ���� ������� ���� ����� web.xml���� ������ �ִ´�
		int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSize"));
		int pageUnit = Integer.parseInt(getServletContext().getInitParameter("pageUnit"));
		searchVO.setPageSize(pageSize);

		// ���ݼ����� display
		String priceSort = CommonUtil.null2str(request.getParameter("priceSort"));
		searchVO.setPriceSort(priceSort);
		System.out.println("searchVO.toString() : " + searchVO.toString());

		// �˻������� �־ ���� �������� list�� �����´�
		//ProductService service = new ProductServiceImpl();
		System.out.println("����� ListProductAction : " + searchVO.toString());
		Map<String, Object> map = productServiceImpl.getProductList(searchVO);

		// rssultPage�� pagingó��
		Page resultPage = new Page(currentPage, ((Integer) map.get("count")).intValue(), pageUnit, pageSize);
		System.out.println("resultPage.toString() : " + resultPage.toString());

		// �˻������� �˻��ؼ� ���� list�� ��´�
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("searchVO", searchVO);
		request.setAttribute("list", map.get("list"));
		request.setAttribute("count", ((List<Product>)map.get("list")).size() );
		request.setAttribute("menu", menu);

		System.out.println("[ListProductAction execute() end...]");
		return "forward:/product/listProduct.jsp";
	}
	*/

}
