package com.example.study.service;

import com.example.study.entity.User;
import com.example.study.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：xjh
 * @date ：Created in 2023/3/28 10:17
 * @description：
 * @modified By：
 * @version:
 */
@Service
public class UserService {

	@Resource
	private UserMapper userMapper;

	public List<User> list(){
		return userMapper.list();
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateUser(String name, int id){
		userMapper.updateUser(name, id);
		int i = 1 / (1 - 1);
	}

}
