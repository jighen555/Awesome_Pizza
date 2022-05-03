package com.awesome.pizza.controller;

import com.awesome.pizza.constants.PizzaOrderConstant;
import com.awesome.pizza.constants.ResultStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class TestControllerTest {

    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test() {
        int status;
        boolean isOk = false;

        try {
            String uri = "/test";
            setUp();
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
            status = mvcResult.getResponse().getStatus();

            String content = mvcResult.getResponse().getContentAsString();
            JSONObject jsonObject = new JSONObject(content);
            if (jsonObject.has("Result") && jsonObject.has("Description")) {
                String result = String.valueOf(jsonObject.get("Result"));
                String description = String.valueOf(jsonObject.get("Description"));
                if (result.equals(ResultStatus.OK.getDescription())
                        && description.equals(PizzaOrderConstant.GOOD_TEST.getDescription()))
                    isOk = true;
            }
        } catch (Exception e) {
            status = 0;
            isOk = false;
        }

        Assertions.assertEquals(200, status);
        Assertions.assertTrue(isOk);
    }
}
