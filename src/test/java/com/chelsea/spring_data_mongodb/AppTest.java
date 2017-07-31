package com.chelsea.spring_data_mongodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.chelsea.spring_data_mongodb.bean.User;
import com.chelsea.spring_data_mongodb.dao.UserDao;

public class AppTest extends TestCase {

	private static UserDao userDaoImpl;
	private static ClassPathXmlApplicationContext app;

	@Override
	public void setUp() throws Exception {
		try {
			app = new ClassPathXmlApplicationContext(
					new String[] { "classpath:applicationContext-mongo.xml" });
			userDaoImpl = (UserDao) app.getBean("userDao");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdd() {
		// 添加user
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setId("" + i);
			user.setAge(i);
			user.setName("zcy" + i);
			user.setPassword("zcy" + i);
			userDaoImpl.insert(user);
		}
	}

	@Test
	public void testFindAll() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("maxAge", 5);
		List<User> list = userDaoImpl.findAll(params);
		System.out.println("user.count()==" + list.size());
	}

	@Test
	public void testUpdate() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", "1");
		User user = userDaoImpl.findOne(params);
		System.out.println("user.name===" + user.getName());
		System.out.println("=============update==================");
		params.put("name", "hello");
		userDaoImpl.update(params);
		user = userDaoImpl.findOne(params);
		System.out.println("user.name===" + user.getName());
	}

	@Test
	public void testRemove() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", "2");
		userDaoImpl.remove(params);
		User user = userDaoImpl.findOne(params);
		System.out.println("user==" + user);
	}

}
