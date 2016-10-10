package com.test.model;

/**
 * Data
 * @author Riviera
 *
 */
public class Data {

	private Object value;
	
	private long timestamp;

	public Data(Object value) {
		super();
		this.value = value;
		timestamp = System.currentTimeMillis();
	}

	public String getValue() {
		return value.toString();
	}

	public long getTimestamp() {
		return timestamp;
	}
}
