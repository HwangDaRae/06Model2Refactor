package com.model2.mvc.common;

public class Search {

	private int curruntPage;
	String searchCondition;
	String searchKeyword;
	int pageSize;
	String priceSort;

	public Search() {
	}

	public Search(int curruntPage, String searchCondition, String searchKeyword, int pageSize) {
		super();
		this.curruntPage = curruntPage;
		this.searchCondition = searchCondition;
		this.searchKeyword = searchKeyword;
		this.pageSize = pageSize;
	}

	public Search(int curruntPage, String searchCondition, String searchKeyword, int pageSize, String priceSort) {
		super();
		this.curruntPage = curruntPage;
		this.searchCondition = searchCondition;
		this.searchKeyword = searchKeyword;
		this.pageSize = pageSize;
		this.priceSort = priceSort;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurruntPage() {
		return curruntPage;
	}

	public void setCurruntPage(int curruntPage) {
		this.curruntPage = curruntPage;
	}

	public String getSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(String searchCondition) {
		this.searchCondition = searchCondition;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public String getPriceSort() {
		return priceSort;
	}

	public void setPriceSort(String priceSort) {
		this.priceSort = priceSort;
	}

	@Override
	public String toString() {
		return "Search [curruntPage=" + curruntPage + ", searchCondition=" + searchCondition + ", searchKeyword="
				+ searchKeyword + ", pageSize=" + pageSize + ", priceSort=" + priceSort + "]";
	}
}