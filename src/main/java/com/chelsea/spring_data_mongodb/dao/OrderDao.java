package com.chelsea.spring_data_mongodb.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.chelsea.spring_data_mongodb.bean.Order;

@Repository("orderDao")
public class OrderDao {

	private final static String COLLECTION = "order";

	@Resource
	private MongoTemplate mongoTemplate;

	public void insert(Order order) {
		mongoTemplate.insert(order);
	}

	public void save(Order order) {
		mongoTemplate.save(order);
	}

	public void insertAll(List<Order> orders) {
		mongoTemplate.insertAll(orders);
	}

	public void remove(Map<String, Object> params) {
		mongoTemplate.remove(
				new Query(Criteria.where("onumber").is(params.get("onumber"))),
				Order.class);
	}

	public void dropCollection() {
		mongoTemplate.dropCollection(COLLECTION);
	}

	public List<Order> find(Map<String, Object> params) {
		Query query = new Query(new Criteria().orOperator(
				Criteria.where("onumber").is(params.get("onumber1")), Criteria
						.where("onumber").is(params.get("onumber2"))));
		List<Order> list = mongoTemplate.find(query, Order.class);
		return list;
	}
}
