package com.chelsea.spring_data_mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.chelsea.spring_data_mongodb.bean.Item;
import com.chelsea.spring_data_mongodb.bean.Order;
import com.chelsea.spring_data_mongodb.dao.OrderDao;

public class OrderTest extends TestCase {

	private static OrderDao orderDao;
	private static ClassPathXmlApplicationContext app;

	@Override
	public void setUp() {
		try {
			app = new ClassPathXmlApplicationContext(
					new String[] { "classpath:applicationContext-mongo.xml" });
			orderDao = (OrderDao) app.getBean("orderDao");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInsert() throws Exception {
		// 订单
		Order order = new Order();
		order.setOnumber("0012");
		order.setDate(new Date());
		order.setCname("zcy");
		// 订单详情
		List<Item> items = new ArrayList<Item>();
		Item item1 = new Item();
		item1.setPnumber("p001");
		item1.setPrice(4.0);
		item1.setQuantity(5);
		items.add(item1);
		Item item2 = new Item();
		item2.setPnumber("p002");
		item2.setPrice(8.0);
		item2.setQuantity(6);
		items.add(item2);
		order.setItems(items);
		orderDao.insert(order);
	}

	@Test
	public void testSave() throws Exception {
		// 订单
		Order order = new Order();
		order.setId("5980231742bdf21798f07ff5");
		order.setOnumber("0013");
		order.setDate(new Date());
		order.setCname("zcy");
		// 订单详情
		List<Item> items = new ArrayList<Item>();
		Item item1 = new Item();
		item1.setPnumber("p001");
		item1.setPrice(4.0);
		item1.setQuantity(5);
		items.add(item1);
		Item item2 = new Item();
		item2.setPnumber("p002");
		item2.setPrice(8.0);
		item2.setQuantity(6);
		items.add(item2);
		order.setItems(items);
		orderDao.save(order);
	}

	@Test
	public void testInsertAll() throws Exception {
		List<Order> orders = new ArrayList<Order>();
		for (int i = 1; i <= 10; i++) {
			// 订单
			Order order = new Order();
			order.setOnumber("00" + i);
			order.setDate(new Date());
			order.setCname("zcy" + i);
			// 订单详情
			List<Item> items = new ArrayList<Item>();
			Item item1 = new Item();
			item1.setPnumber("p00" + i);
			item1.setPrice(4.0 + i);
			item1.setQuantity(5 + i);
			items.add(item1);
			Item item2 = new Item();
			item2.setPnumber("p00" + (i + 1));
			item2.setPrice(8.0 + i);
			item2.setQuantity(6 + i);
			items.add(item2);
			order.setItems(items);
			orders.add(order);
		}
		orderDao.insertAll(orders);
	}

	@Test
	public void testRemove() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("onumber", "0013");
		orderDao.remove(params);
	}

	@Test
	public void testDropCollection() {
		orderDao.dropCollection();
	}

}
