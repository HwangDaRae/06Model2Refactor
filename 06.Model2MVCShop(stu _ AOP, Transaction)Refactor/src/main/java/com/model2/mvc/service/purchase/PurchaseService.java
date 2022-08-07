package com.model2.mvc.service.purchase;

import java.util.List;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;

public interface PurchaseService {
	
	public Purchase addPurchase(Purchase purchase, Product product) throws Exception;
	
	public Purchase getPurchase(int tranNo) throws Exception;
	
	public List<Purchase> getPurchaseFromTranId(String tranId) throws Exception;
	
	public int totalCountPurchaseList(String userId) throws Exception;
	
	public List<Purchase> getPurchaseList(Search searchVO, String userId) throws Exception;
	
	public Purchase updatePurchase(Purchase purchaseVO) throws Exception;
	
	public void updateTranCode(Purchase purchaseVO) throws Exception;
	
	public List<Purchase> getListPurchase(String tranId) throws Exception;
}
