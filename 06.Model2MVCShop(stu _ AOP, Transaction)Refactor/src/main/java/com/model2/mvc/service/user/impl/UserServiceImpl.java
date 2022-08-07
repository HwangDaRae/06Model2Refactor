package com.model2.mvc.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.user.UserDao;
import com.model2.mvc.service.user.UserService;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
	
	@Autowired
	@Qualifier("userDaoImpl")
	UserDao userDao;

	public UserServiceImpl() {
		System.out.println("¿©±â´Â UserServiceImpl default Constructor");
	}

	@Override
	public void addUser(User user) throws Exception {
		userDao.addUser(user);
	}

	@Override
	public User loginUser(User userVO) throws Exception {
		System.out.println(getClass().getName() + ".loginUser(User userVO)");
		return userDao.loginUser(userVO);
	}

	@Override
	public User getUser(String userId) throws Exception {
		return userDao.getUser(userId);
	}

	@Override
	public List<User> getUserList(Search searchVO) throws Exception {
		return userDao.getUserList(searchVO);
	}

	@Override
	public int totalCount(Search search) throws Exception {
		return userDao.totalCount(search);
	}

	@Override
	public void updateUser(User user) throws Exception {
		userDao.updateUser(user);
	}

	@Override
	public boolean checkDuplication(String userId) throws Exception {
		return userDao.checkDuplication(userId);
	}

}
