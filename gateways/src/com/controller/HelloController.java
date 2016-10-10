package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@RequestMapping(value="/test/{name}", method = RequestMethod.GET)
	@ResponseBody
	public String hello(@PathVariable("name")String name) {
		System.out.println(name);
		return name;
	}
}
