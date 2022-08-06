package com.model2.mvc.service.user.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.user.UserDao;

@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {
	
	@Autowired
	@Qualifier("sqlSessionTemplate")
	SqlSession sqlSession;

	public UserDaoImpl() {
		System.out.println("����� UserDaoImpl default Constructor");
	}

	@Override
	public void addUser(User user) throws Exception {
		sqlSession.insert("UserMapper.addUser", user);
	}

	@Override
	public User loginUser(User userVO) throws Exception {
		User dbUser=(User)sqlSession.selectList("UserMapper.getUser", userVO.getUserId());

		//���̵�� ã�� db������ ��й�ȣ�� ����ڰ� �Է��� ��й�ȣ�� ��ġ�ϴ��� Ȯ��
		if(! dbUser.getPassword().equals(userVO.getPassword()))
			throw new Exception("�α��ο� �����߽��ϴ�.");
		
		return dbUser;
	}

	@Override
	public User getUser(String userId) throws Exception {
		return (User)sqlSession.selectOne("UserMapper.findUser", userId);
	}

	@Override
	public List<User> getUserList(Search searchVO) throws Exception {
		List<User> list = sqlSession.selectList("UserMapper.allUser", searchVO);
		return list;
	}

	@Override
	public int totalCount(Search search) throws Exception {
		return sqlSession.selectOne("UserMapper.totalCount", search);
	}

	@Override
	public void updateUser(User user) throws Exception {
		sqlSession.update("UserMapper.updateUser", user);
	}

	@Override
	public boolean checkDuplication(String userId) throws Exception {
		boolean result=true;
		User userVO=(User)sqlSession.selectList("UserMapper.getUser", userId);
		if(userVO != null) {
			result=false;
		}
		return result;
	}

}
