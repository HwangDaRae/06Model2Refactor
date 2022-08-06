package com.model2.mvc.service.purchase;

import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;

public interface PurchaseDao {
	
	public Purchase addPurchase(Purchase purchase) throws Exception;
	
	public Purchase getPurchase(int tranNo) throws Exception;
	
	public int totalCountPurchaseList(String userId) throws Exception;
	
	public List<Purchase> getPurchaseList(Search searchVO, String userID) throws Exception;
	
	public Purchase updatePurchase(Purchase purchaseVO) throws Exception;
	
	public void updateTranCode(Purchase purchaseVO) throws Exception;
	
	public List<Purchase> getListPurchase(String tranId) throws Exception;

}
