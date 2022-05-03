package com.awesome.pizza.controller;

import com.awesome.pizza.constants.PizzaOrderConstant;
import com.awesome.pizza.constants.ResultStatus;
import com.awesome.pizza.constants.WorkingProgressStatus;
import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.model.PizzaOrder;
import com.awesome.pizza.service.PizzaOrderService;
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
public class PizzaOrderControllerTest {

    private final String path = "/pizza-order";

    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private PizzaOrderService pizzaOrderService;

    @Test
    public void getAllTest() {
        String uri = path + "/all";

        List<PizzaOrder> pizzaOrders = pizzaOrderService.getAll();
        if (pizzaOrders != null && !pizzaOrders.isEmpty()) {
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
                JSONArray jsonArray = object.getJSONArray("Orders");
                lenght = jsonArray.length();

                status =  response.getStatus();
            } catch (Exception e) {
                status = 0;
                lenght = 0;
            }

            Assertions.assertEquals(200, status);
            Assertions.assertEquals(pizzaOrders.size(), lenght);
        } else {
            int status;
            try {
                mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                MvcResult mvcResult =
                        mvc
                                .perform(MockMvcRequestBuilders
                                        .get(uri)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andReturn();

                status =  mvcResult.getResponse().getStatus();
            } catch (Exception e) {
                status = 0;
            }

            Assertions.assertEquals(204, status);
        }
    }

    @Test
    public void processedOrder() {
        String uri = path + "/add";

        List<Pizza> pizzas = pizzaService.getAll();
        PizzaOrder pizzaOrder = new PizzaOrder();

        if (pizzas != null && !pizzas.isEmpty()) {
            Pizza pizza = pizzas.get(0);
            int status;
            String result = "";
            String data = null;
            String identifier = "";
            String description = "";
            try {
                String request = "{ \"pizzas\" : [\"" + pizza.getUuid() + "\"] }";
                mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                MvcResult mvcResult =
                        mvc
                                .perform(MockMvcRequestBuilders
                                        .post(uri)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .content(request))
                                .andReturn();

                MockHttpServletResponse response = mvcResult.getResponse();
                String content = response.getContentAsString();
                JSONObject object = new JSONObject(content);
                if (object.has("Result")) { result = String.valueOf(object.get("Result")); }
                if (object.has("Data")) { data = String.valueOf(object.get("Data")); }
                JSONObject jsonObject = object.getJSONObject("Data");
                if (jsonObject.has("identifier")) { identifier = String.valueOf(jsonObject.get("identifier")); }

                status =  response.getStatus();
            } catch (Exception e) {
                status = 0;
                result = ResultStatus.KO.toString();
            }

            Assertions.assertEquals(200, status);
            Assertions.assertEquals(ResultStatus.OK.toString(), result);
            Assertions.assertNotNull(data);

            if (identifier != null && !identifier.isEmpty()) {
                try {
                    uri = path + "/takeCharge/" + identifier;

                    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                    MvcResult mvcResult = mvc
                            .perform(MockMvcRequestBuilders
                                    .post(uri)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andReturn();

                    MockHttpServletResponse response = mvcResult.getResponse();
                    String content = response.getContentAsString();
                    JSONObject object = new JSONObject(content);
                    if (object.has("Result")) { result = String.valueOf(object.get("Result")); }
                    if (object.has("Description")) { description = String.valueOf(object.get("Description")); }

                    status =  response.getStatus();

                    pizzaOrder = pizzaOrderService.getByIdentifier(identifier);
                } catch (Exception e) {
                    status = 0;
                    result = ResultStatus.KO.toString();
                }

                Assertions.assertEquals(200, status);
                Assertions.assertEquals(ResultStatus.OK.toString(), result);
                Assertions.assertEquals(PizzaOrderConstant.SUCCESS_TAKING_CHARGE.getDescription(), description);
                Assertions.assertEquals(pizzaOrder.getStatus().getId(), WorkingProgressStatus.TAKE_CHARGE.getId());

                try {
                    uri = path + "/processed/" + identifier;

                    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                    MvcResult mvcResult = mvc
                            .perform(MockMvcRequestBuilders
                                    .post(uri)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andReturn();

                    MockHttpServletResponse response = mvcResult.getResponse();
                    String content = response.getContentAsString();
                    JSONObject object = new JSONObject(content);
                    if (object.has("Result")) { result = String.valueOf(object.get("Result")); }
                    if (object.has("Description")) { description = String.valueOf(object.get("Description")); }

                    status =  response.getStatus();

                    pizzaOrder = pizzaOrderService.getByIdentifier(identifier);
                } catch (Exception e) {
                    status = 0;
                    result = ResultStatus.KO.toString();
                }

                Assertions.assertEquals(200, status);
                Assertions.assertEquals(ResultStatus.OK.toString(), result);
                Assertions.assertEquals(PizzaOrderConstant.SUCCESS_PROCESSED.getDescription(), description);
                Assertions.assertEquals(pizzaOrder.getStatus().getId(), WorkingProgressStatus.PROCESSED.getId());

                pizzaOrderService.delete(pizzaOrder);
            } else {
                throw new RuntimeException("Identifier is null or empty");
            }
        }
    }

    @Test
    public void addKO() {
        String uri = path + "/add";

        int status;
        String result = "";
        try {
            String request = "{ \"pizzas\" : [\" TEST \"] }";
            mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            MvcResult mvcResult =
                    mvc
                            .perform(MockMvcRequestBuilders
                                    .post(uri)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content(request))
                            .andReturn();

            MockHttpServletResponse response = mvcResult.getResponse();
            String content = response.getContentAsString();
            JSONObject object = new JSONObject(content);
            if (object.has("Result")) { result = String.valueOf(object.get("Result")); }

            status =  response.getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(200, status);
        Assertions.assertEquals(ResultStatus.KO.toString(), result);
    }

    @Test
    public void status() {
        String uri = path + "/status";

        List<PizzaOrder> pizzaOrders = pizzaOrderService.getAll();

        if (pizzaOrders != null && !pizzaOrders.isEmpty()) {
            for (PizzaOrder pizzaOrder : pizzaOrders) {
                String identifier = pizzaOrder.getIdentifier();

                int status;
                String result = "";
                String description = "";
                String statusPizzaOrder = "";
                try {
                    String URL = uri;
                    if (identifier != null) { URL +=  "/" + identifier; }

                    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                    MvcResult mvcResult = mvc
                            .perform(MockMvcRequestBuilders
                                    .get(URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andReturn();

                    MockHttpServletResponse response = mvcResult.getResponse();
                    String content = response.getContentAsString();
                    JSONObject object = new JSONObject(content);
                    if (object.has("Result")) { result = String.valueOf(object.get("Result")); }
                    if (object.has("Description")) { description = String.valueOf(object.get("Description")); }
                    if (object.has("Status")) { statusPizzaOrder = String.valueOf(object.get("Status")); }

                    status =  mvcResult.getResponse().getStatus();
                } catch (Exception e) {
                    status = 0;
                }

                Assertions.assertEquals(200, status);
                Assertions.assertEquals(ResultStatus.OK.toString(), result);
                Assertions.assertEquals(PizzaOrderConstant.PIZZA_ORDER_OK.getDescription(), description);
                Assertions.assertEquals(pizzaOrder.getStatus().getDescription(), statusPizzaOrder);
            }
        } else {
            int status;
            try {
                mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                MvcResult mvcResult = mvc
                        .perform(MockMvcRequestBuilders
                                .get(uri)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                status =  mvcResult.getResponse().getStatus();
            } catch (Exception e) {
                status = 0;
            }

            Assertions.assertEquals(204, status);
        }
    }

    @Test
    public void oldestPending() {
        String uri = path + "/oldestPending";

        PizzaOrder pizzaOrder = pizzaOrderService.getOldPending();

        if (pizzaOrder != null) {
            int status;
            String result = "";
            String description = "";
            String identifier = "";
            try {
                mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                MvcResult mvcResult = mvc
                        .perform(MockMvcRequestBuilders
                                .get(uri)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                MockHttpServletResponse response = mvcResult.getResponse();
                String content = response.getContentAsString();
                JSONObject object = new JSONObject(content);
                if (object.has("Result")) { result = String.valueOf(object.get("Result")); }
                if (object.has("Description")) { description = String.valueOf(object.get("Description")); }
                if (object.has("Identifier")) { identifier = String.valueOf(object.get("Identifier")); }

                status =  mvcResult.getResponse().getStatus();
            } catch (Exception e) {
                status = 0;
            }

            Assertions.assertEquals(200, status);
            Assertions.assertEquals(ResultStatus.OK.toString(), result);
            Assertions.assertEquals(PizzaOrderConstant.OLDEST_PENDING.getDescription(), description);
            Assertions.assertEquals(pizzaOrder.getIdentifier(), identifier);
        } else {
            int status;
            try {
                mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                MvcResult mvcResult = mvc
                        .perform(MockMvcRequestBuilders
                                .get(uri)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                status =  mvcResult.getResponse().getStatus();
            } catch (Exception e) {
                status = 0;
            }

            Assertions.assertEquals(204, status);
        }
    }
}
