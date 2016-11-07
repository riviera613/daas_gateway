package com.model;

import org.json.JSONObject;

import com.model.base.AbstractDevice;

/**
 * ARDrone无人机
 * @author Riviera
 *
 */
public class ARDrone extends AbstractDevice {

	public ARDrone(String id) {
		super(id);
	}

	@Override
	public String control(String type) {
		return ERROR;
	}

	@Override
	public String control(String type, JSONObject params) {
		return ERROR;
	}

	@Override
	public void handler(String msg) {

	}

}
