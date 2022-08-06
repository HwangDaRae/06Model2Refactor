package com.model2.mvc.service.user;

import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.User;

public interface UserService {
	
	public void addUser(User user) throws Exception;
	
	public User loginUser(User userVO) throws Exception;
	
	public User getUser(String userId) throws Exception;

	public List<User> getUserList(Search searchVO) throws Exception;
	
	public int totalCount(Search search) throws Exception;
	
	public void updateUser(User user) throws Exception;
	
	public boolean checkDuplication(String userId) throws Exception;
}
