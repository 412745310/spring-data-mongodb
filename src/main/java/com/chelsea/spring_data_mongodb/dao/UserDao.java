package com.chelsea.spring_data_mongodb.dao;

import java.util.List;
import java.util.Map;

import com.chelsea.spring_data_mongodb.bean.User;

public interface UserDao {
	
	public void insert(User object);

	public User findOne(Map<String, Object> params);

	public List<User> findAll(Map<String, Object> params);

	public void update(Map<String, Object> params);

	public void createCollection(String name);

	public void remove(Map<String, Object> params);
}
