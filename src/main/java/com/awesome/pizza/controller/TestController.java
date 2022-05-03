package com.awesome.pizza.controller;

import com.awesome.pizza.constants.PizzaOrderConstant;
import com.awesome.pizza.constants.ResultStatus;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Test controller to see if the API running
 */
@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> test() {
		JSONObject object = new JSONObject();
		object.put("Result", ResultStatus.OK);
		object.put("Description", PizzaOrderConstant.GOOD_TEST.getDescription());

		return object.toMap();
	}
}
