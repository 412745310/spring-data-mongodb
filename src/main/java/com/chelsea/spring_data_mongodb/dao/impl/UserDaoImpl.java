package com.chelsea.spring_data_mongodb.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.chelsea.spring_data_mongodb.bean.User;
import com.chelsea.spring_data_mongodb.dao.UserDao;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

	private static String COLLECTION_NAME = "users";

	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public void insert(User object) {
		mongoTemplate.insert(object, COLLECTION_NAME);
	}

	@Override
	public User findOne(Map<String, Object> params) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("id").is(params.get("id"))),
				User.class, COLLECTION_NAME);
	}

	@Override
	public List<User> findAll(Map<String, Object> params) {
		List<User> result = mongoTemplate.find(new Query(Criteria.where("age")
				.lt(params.get("maxAge"))), User.class, COLLECTION_NAME);
		return result;
	}

	@Override
	public void update(Map<String, Object> params) {
		mongoTemplate.upsert(new Query(Criteria.where("id")
				.is(params.get("id"))), new Update().set("name",
				params.get("name")), User.class, COLLECTION_NAME);
	}

	@Override
	public void createCollection(String name) {
		mongoTemplate.createCollection(name);
	}

	@Override
	public void remove(Map<String, Object> params) {
		mongoTemplate.remove(new Query(Criteria.where("id")
				.is(params.get("id"))), User.class, COLLECTION_NAME);
	}
}
