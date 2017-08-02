package com.chelsea.spring_data_mongodb.bean;

import com.google.code.morphia.annotations.Id;

public class TotalResult {

	@Id
	private String id;
	private Integer total;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

}
