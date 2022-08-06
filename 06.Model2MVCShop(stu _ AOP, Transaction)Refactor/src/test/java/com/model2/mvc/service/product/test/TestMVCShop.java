package com.model2.mvc.service.product.test;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/context-aspect.xml", "classpath:config/context-common.xml", "classpath:config/context-mybatis.xml", "classpath:config/context-transaction.xml" })
public class TestMVCShop {
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("cartServiceImpl")
	private CartService cartService;

	//@Test
	public void testUser() throws Exception {
		System.out.println("\n===================================");
		User user = new User("userIdTest01", "userNameTest01", "password", null, null, null, null, null, null);
		Search search = new Search(2, null, null, 3);
				
		System.out.println(":: 1 : User ȸ������");
		//userService.addUser(user);

		System.out.println(":: 2 : User �Ѹ� ã��");
		System.out.println(userService.getUser("user02"));

		System.out.println(":: 3 : User �� �Խù� ��");
		System.out.println(userService.totalCount(search));

		System.out.println(":: 4 : User ���� ������ �Խù�");
		List<User> list = (List<User>)userService.getUserList(search);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString());
		}

		User vo = new User("userIdTest01", "userNameUpdate", "passwordUpdate", null, null, null, null, null, null);
		System.out.println(":: 5 : User ���� ����");
		//userService.updateUser(vo);
		System.out.println("===================================\n");
	}
	
	@Test
	public void testProduct() throws Exception {
		System.out.println("\n===================================");
		Product product = new Product(0, "ǳ��", "��ǳ��", "2022-07-30", 461000, "/images/uploadFiles/test2.jpg", null, 27);
		Search search = new Search(2, null, null, 3, null);
		Map<String, Object> map = new HashMap<String, Object>();

		//System.out.println("��ǰ��� : " + productService.addProduct(product));
		System.out.println("��ǰ��ȣ�� ��ǰ �ϳ� ã�� : " + productService.getProduct(10001));
		System.out.println("�� ��ǰ ���� �������� : " + productService.getProductTotalCount(search));
		/*
		System.out.println("��ǰ ��ü �������� : ");
		map = productService.getProductList(search);
		List<Product> list = new ArrayList<Product>();
		list = (List<Product>) map.get("list");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString());
		}
		*/
		//Product productUpdate = new Product(10038, "ǳ��update", "��ǳ��update", "2022-07-30", 461000, "/images/uploadFiles/test2.jpg", null, 27);
		// System.out.println("��ǰ ���� ���� : " +
		// productService.updateProduct(productUpdate));
		System.out.println("===================================\n");
	}

	/*
		private int tranNo;
		private String tranId;
		private Product purchaseProd;
		private User buyer;
		private String paymentOption;
		private String receiverName;
		private String receiverPhone;
		private String divyAddr;
		private String divyRequest;
		private String tranCode;
		private Date orderDate;
		private String divyDate;
		private int amount;
		private int totalPrice;
	*/
	//@Test
	public void testPurchase() throws Exception {
		System.out.println("\n===================================");
		User user = new User("user02", "SCOTT", "2222", "user", null, null, null, null, null);		
		Product product = new Product(10038, "ǳ��update", "��ǳ��update", "2022-07-30", 461000,
				"/images/uploadFiles/test2.jpg", null, 27);
		//Product product = new Product(10038, "ǳ��update", "��ǳ��update", "2022-07-30", 461000,
		//				"/images/uploadFiles/test2.jpg", null, 0);
		Purchase purchase = new Purchase(0, product, user, "2", "�޴»���̸�", "����ó", "����ּ�", "��ۿ䱸����", "0", null, "2022-07-30", 3);
		//System.out.println("1 ��ǰ���� : " + (purchaseService.addPurchase(purchase, product)).toString());
		
		System.out.println("2 ���Ź�ȣ�� ���Ż��������� : " + purchaseService.getPurchase(10043));
		
		System.out.println("3 ��ü ���� Ƚ�� : " + purchaseService.totalCountPurchaseList("user02"));
		
		System.out.println("4 ������������ ���� ����Ʈ : ");
		Search search = new Search(1, null, null, 3, null);
		
		List<Purchase> purList = purchaseService.getPurchaseList(search, user.getUserId());
		//
		
		purchase = new Purchase(10043, product, user, "2", "�̸�update", "����ó", "����ּ�", "��ۿ䱸����", "0", null, "2022-07-30", 3);
		//System.out.println("5 ������������ : " + purchaseService.updatePurchase(purchase));
		
		Product product2 = new Product(10038, "ǳ��update", "��ǳ��update", "2022-07-30", 461000,
								"/images/uploadFiles/test2.jpg", null, 0);
		Purchase purchase2 = new Purchase(0, product2, user, "2", "�޴»���̸�", "����ó", "����ּ�", "��ۿ䱸����", "0", null, "2022-07-30", 3);
		System.out.println("6 �����ڵ���� : ");
		//purchaseService.updateTranCode(purchase2);
		
		System.out.println("===================================\n");
	}

	//@Test
	public void testCart() throws Exception {
		System.out.println("\n===================================");
		Cart cart = new Cart(10000, "user02", "", "�������", "�������2", 5, 12000, 14);
		cart = new Cart(10001, "user02", "", "�������", "�������2", 5, 12000, 14);
		System.out.println("1 ��ٱ��� ��� : ");
		//cartService.insertCart(cart);
		
		Map<String, Object> map = new HashMap<String, Object>();
		int[] deleteArray = new int[2];
		System.out.println(deleteArray.length);
		deleteArray[0] = 10000;
		deleteArray[1] = 10001;
		System.out.println("2 ��ٱ��� ���� : ");
		map.put("deleteArray", deleteArray);
		map.put("user_id", "user02");
		//cartService.deleteCart(map);
		
		System.out.println("3 ��ٱ��� ���� ���� : ");
		cartService.updateAmount(cart);
		
		System.out.println("4 ��ٱ��� ��ü ���� : " + cartService.totalCountCartList("user02"));
		
		System.out.println("5 ��ٱ��� ��ü �Խù� : ");
		List<Cart> list = cartService.getCartList("user02");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString());
		}
		System.out.println("===================================\n");
	}

}
