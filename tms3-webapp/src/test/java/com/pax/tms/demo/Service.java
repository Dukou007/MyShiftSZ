package com.pax.tms.demo;

public class Service {
	private Dao dao;

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	/**
	 * 根据存货量判断货物是否畅销
	 * 
	 * @param group
	 * @return
	 */
	public Status checkStatus(String group) {
		int count = this.dao.getStoreCount(group);

		if (count <= 0) {
			return Status.UNKOWN;
		} else if (count <= 800) {
			return Status.UNSALABLE;
		} else if (count <= 1000) {
			return Status.NORMAL;
		} else {
			return Status.SELLINGWELL;
		}
	}
}
