package com.awesome.pizza.controller;

import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.service.PizzaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@SpringBootTest
public class PizzaControllerTest {

    private final String path = "/pizza";

    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private PizzaService pizzaService;

    @Test
    public void getAllTest() {
        String uri = path + "/all";

        List<Pizza> pizzas = pizzaService.getAll();
        if (pizzas != null && !pizzas.isEmpty()) {
            int status;
            int lenght;
            try {
                mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                MvcResult mvcResult =
                        mvc
                        .perform(MockMvcRequestBuilders
                                .get(uri)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                MockHttpServletResponse response = mvcResult.getResponse();
                String content = response.getContentAsString();
                JSONObject object = new JSONObject(content);
                JSONArray jsonArray = object.getJSONArray("Pizzas");
                lenght = jsonArray.length();

                status =  response.getStatus();
            } catch (Exception e) {
                status = 0;
                lenght = 0;
            }

            Assertions.assertEquals(200, status);
            Assertions.assertEquals(pizzas.size(), lenght);
        }
    }

    @Test
    public void getByUuidTest() {
        List<Pizza> pizzas = pizzaService.getAll();

        if (pizzas != null && !pizzas.isEmpty()) {
            for (Pizza pizza : pizzas) {
                String uuid = pizza.getUuid();

                int status;
                String pizzaName = "";
                String pizzaDescription = "";
                try {
                    String URL = path;
                    if (uuid != null) { URL +=  "/" + uuid; }

                    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                    MvcResult mvcResult = mvc
                            .perform(MockMvcRequestBuilders
                                    .get(URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andReturn();

                    MockHttpServletResponse response = mvcResult.getResponse();
                    String content = response.getContentAsString();
                    JSONObject object = new JSONObject(content);
                    if (object.has("name")) { pizzaName = String.valueOf(object.get("name")); }
                    if (object.has("description")) { pizzaDescription = String.valueOf(object.get("description")); }

                    status =  response.getStatus();
                } catch (Exception e) {
                    status = 0;
                }

                Assertions.assertEquals(200, status);
                Assertions.assertEquals(pizzaName, pizza.getName());
                Assertions.assertEquals(pizzaDescription, pizza.getDescription());
            }
        }
    }

    /**
     * Test with a uuid that doesn't exist
     */
    @Test
    public void getByUuidNullTest() {
        String uuid = "TEST";

        int status;
        try {
            mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders
                                .get(path + "/" + uuid)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            status =  mvcResult.getResponse().getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(204, status);
    }
}
