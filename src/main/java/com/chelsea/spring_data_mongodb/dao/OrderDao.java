package com.chelsea.spring_data_mongodb.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.chelsea.spring_data_mongodb.bean.Order;
import com.chelsea.spring_data_mongodb.bean.PageModel;
import com.chelsea.spring_data_mongodb.bean.TotalResult;
import com.google.code.morphia.Morphia;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository("orderDao")
public class OrderDao {

	private final static String COLLECTION = "order";

	private Morphia morphia;

	public OrderDao() {
		morphia = new Morphia();
		morphia.map(Order.class);
	}

	@Resource
	private MongoTemplate mongoTemplate;

	/**
	 * 新增，id重复时报错
	 * 
	 * @param order
	 */
	public void insert(Order order) {
		mongoTemplate.insert(order);
	}

	/**
	 * 新增或更新，id重复时更新
	 * 
	 * @param order
	 */
	public void save(Order order) {
		mongoTemplate.save(order);
	}

	/**
	 * 批量新增
	 * 
	 * @param orders
	 */
	public void insertAll(List<Order> orders) {
		mongoTemplate.insertAll(orders);
	}

	/**
	 * 删除
	 * 
	 * @param params
	 */
	public void remove(Map<String, Object> params) {
		mongoTemplate.remove(
				new Query(Criteria.where("onumber").is(params.get("onumber"))),
				Order.class);
	}

	public void dropCollection() {
		mongoTemplate.dropCollection(COLLECTION);
	}

	/**
	 * 查询
	 * 
	 * @param params
	 * @return
	 */
	public List<Order> find(Map<String, Object> params) {
		Query query = new Query(Criteria.where("").orOperator(
				Criteria.where("onumber").is(params.get("onumber1")),
				Criteria.where("onumber").is(params.get("onumber2"))));
		List<Order> list = mongoTemplate.find(query, Order.class);
		return list;
	}

	/**
	 * 更新匹配where条件的第一条数据
	 * 
	 * @param params
	 */
	public void updateFirst(Map<String, Object> params) {
		String cname = (String) params.get("cname");
		String date = (String) params.get("date");
		Criteria criteria = Criteria.where("cname").is(cname);
		Query query = new Query(criteria);
		Update update = new Update();
		update.set("date", date);
		mongoTemplate.updateFirst(query, update, Order.class);
	}

	/**
	 * 修改或新增,where条件匹配不到就新增
	 * 
	 * @param params
	 */
	public void upsert(Map<String, Object> params) {
		String cname = (String) params.get("cname");
		String date = (String) params.get("date");
		Criteria criteria = Criteria.where("cname").is(cname);
		Query query = new Query(criteria);
		Update update = new Update();
		update.set("date", date);
		mongoTemplate.upsert(query, update, Order.class);
	}

	/**
	 * 多条记录修改
	 * 
	 * @param params
	 */
	public void updateMulti(Map<String, Object> params) {
		String date = (String) params.get("date");
		Query query = new Query();
		Update update = new Update();
		update.set("date", date);
		mongoTemplate.updateMulti(query, update, Order.class);
	}

	/**
	 * 通过com.mongodb分页查询
	 * 
	 * @param page
	 * @param params
	 * @return
	 */
	public PageModel<Order> getOrder(PageModel<Order> page,
			Map<String, Object> params) {
		String min = (String) params.get("min");
		String max = (String) params.get("max");
		DBObject queryObject = new BasicDBObject();
		BasicDBList basicDBList = new BasicDBList();
		basicDBList.add(new BasicDBObject("onumber", new BasicDBObject("$gt",
				min)));
		basicDBList.add(new BasicDBObject("onumber", new BasicDBObject("$lt",
				max)));
		queryObject.put("$and", basicDBList);
		DBObject filterDBObject = new BasicDBObject();
		filterDBObject.put("_id", 1);
		filterDBObject.put("cname", 1);
		filterDBObject.put("onumber", 1);
		DBCursor dbCursor = mongoTemplate.getCollection(COLLECTION).find(
				queryObject, filterDBObject);
		// 排序
		DBObject sortDBObject = new BasicDBObject();
		sortDBObject.put("onumber", 1);
		dbCursor.sort(sortDBObject);
		// 分页查询
		dbCursor.skip(page.getSkip()).limit(page.getPageSize());

		// 总数
		int count = dbCursor.count();
		// 循环指针
		List<Order> datas = new ArrayList<Order>();
		while (dbCursor.hasNext()) {
			datas.add(morphia.fromDBObject(Order.class, dbCursor.next()));
		}
		page.setRowCount(count);
		page.setDatas(datas);
		return page;
	}

	/**
	 * 通过com.mongodb聚合
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TotalResult> getAggregationByMongo(Map<String, Object> params) {
		Set<String> onumberSet = (Set<String>) params.get("onumberSet");
		Integer totalMin = (Integer) params.get("totalMin");
		Morphia morphia = new Morphia();
		morphia.map(TotalResult.class);
		// 过滤条件
		DBObject queryObject = new BasicDBObject("onumber", new BasicDBObject(
				"$in", onumberSet));
		DBObject queryMatch = new BasicDBObject("$match", queryObject);
		// 展开数组
		DBObject queryUnwind = new BasicDBObject("$unwind", "$items");
		// 分组统计
		DBObject groupObject = new BasicDBObject();
		groupObject.put("_id", "$items.pnumber");
		groupObject.put("total", new BasicDBObject("$sum", "$items.quantity"));
		DBObject queryGroup = new BasicDBObject("$group", groupObject);
		// 过滤条件
		DBObject finalizeMatch = new BasicDBObject("$match", new BasicDBObject(
				"total", new BasicDBObject("$gt", totalMin)));
		List<DBObject> listDBObject = new ArrayList<DBObject>();
		listDBObject.add(queryMatch);
		listDBObject.add(queryUnwind);
		listDBObject.add(queryGroup);
		listDBObject.add(finalizeMatch);
		AggregationOutput output = mongoTemplate.getCollection(COLLECTION)
				.aggregate(listDBObject);
		List<TotalResult> datas = new ArrayList<TotalResult>();
		for (Iterator<DBObject> iterator = output.results().iterator(); iterator
				.hasNext();) {
			DBObject obj = iterator.next();
			datas.add(morphia.fromDBObject(TotalResult.class, obj));
		}
		return datas;
	}

	/**
	 * 通过spring-data聚合
	 * 
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	public List<TotalResult> getAggregationBySpringData(
			Map<String, Object> params) {
		Morphia morphia = new Morphia();
		morphia.map(TotalResult.class);
		Set<String> onumberSet = (Set<String>) params.get("onumberSet");
		Integer totalMin = (Integer) params.get("totalMin");
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("onumber").in(onumberSet)),
				Aggregation.unwind("items"), Aggregation.group("items.pnumber")
						.sum("items.quantity").as("total"),
				Aggregation.match(Criteria.where("total").gt(totalMin)));

		AggregationResults<BasicDBObject> outputType = mongoTemplate.aggregate(
				agg, COLLECTION, BasicDBObject.class);
		List<TotalResult> datas = new ArrayList<TotalResult>();
		for (Iterator<BasicDBObject> iterator = outputType.iterator(); iterator
				.hasNext();) {
			DBObject obj = iterator.next();
			datas.add(morphia.fromDBObject(TotalResult.class, obj));
		}
		return datas;
	}

}
