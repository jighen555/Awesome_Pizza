package com.awesome.pizza.controller;

import com.awesome.pizza.constants.ResultStatus;
import com.awesome.pizza.constants.WorkingProgressStatus;
import com.awesome.pizza.dto.PizzaOrderDTO;
import com.awesome.pizza.manager.PizzaOrderManager;
import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.model.PizzaOrder;
import com.awesome.pizza.service.PizzaService;
import com.awesome.pizza.service.PizzaOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pizza-order")
public class PizzaOrderController {

    @Autowired
    private PizzaOrderService pizzaOrderService;

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private PizzaOrderManager pizzaOrderManager;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<Pizza> pizzas = pizzaService.getAll();
            List<PizzaOrder> pizzaOrders = pizzaOrderService.getAll();

            if (pizzaOrders.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                Map<String, List<PizzaOrderDTO>> map = new LinkedHashMap<>();
                map.put("Orders", pizzaOrderManager.fillPizzas(pizzaOrders, pizzas));
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<PizzaOrder> getByUuid(@PathVariable("uuid") String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            PizzaOrder pizzaOrder = pizzaOrderService.getByUuid(uuid);

            if (pizzaOrder == null)
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            else
                return new ResponseEntity<>(pizzaOrder, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Controller for add a new order.
     * @param pizzas: List of Pizza's uuids.
     * @return JSON result
     */
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Map<String,List<String>> pizzas) {
        try {
            PizzaOrder pizzaOrder = pizzaOrderManager.generatePizzaOrderPenging(pizzas);
            if (pizzaOrder != null) {
                boolean saved = pizzaOrderService.save(pizzaOrder);
                if (saved) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("Result", ResultStatus.OK.toString());
                    map.put("Description", "The PizzaOrder record was saved correctly");
                    map.put("Data", pizzaOrder);
                    return new ResponseEntity<>(map, HttpStatus.OK);
                } else {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", "The PizzaOrder record was not saved");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("Result", "KO");
                map.put("Description", "The PizzaOrder record is not correct");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method for obtaining the order status
     */
    @GetMapping("/status/{identifier}")
    public ResponseEntity<?> status(@PathVariable("identifier") String identifier) {
        try {
            if (identifier == null || identifier.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            PizzaOrder pizzaOrder = pizzaOrderService.getByIdentifier(identifier);

            if (pizzaOrder == null) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                Map<String, Object> map = pizzaOrderManager.generateMapForStatus(pizzaOrder);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method that searches for the oldest pending order
     */
    @GetMapping("/oldestPending")
    public ResponseEntity<?> oldestPending() {
        try {
            PizzaOrder pizzaOrder = pizzaOrderService.getOldPending();
            if (pizzaOrder == null) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                Map<String, Object> map = pizzaOrderManager.generateMapForOldestPending(pizzaOrder);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method for taking charge of an order and making it change from pending status to take_charge status
     * @param identifier: WorkingProgress identifier
     */
    @PostMapping("/takeCharge/{identifier}")
    public ResponseEntity<?> takeCharge(@PathVariable("identifier") String identifier) {
        try {
            if (identifier == null || identifier.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            PizzaOrder pizzaOrder = pizzaOrderService.getByIdentifier(identifier);

            if (pizzaOrder == null) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                WorkingProgressStatus statusTMP = pizzaOrder.getStatus();
                if (pizzaOrder.getStatus().equals(WorkingProgressStatus.PENDING))
                    pizzaOrder.setStatus(WorkingProgressStatus.TAKE_CHARGE);

                Map<String, Object> map;
                boolean saved = pizzaOrderService.save(pizzaOrder);
                if (saved) {
                    map = pizzaOrderManager.generateMapForTakeCharge(pizzaOrder, statusTMP);
                } else {
                    map = new LinkedHashMap<>();
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", "The order was not taking charge due to technical problems");
                }
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method for processed an order and making it change from take_charge status to processed status
     * @param identifier: WorkingProgress identifier
     */
    @PostMapping("/processed/{identifier}")
    public ResponseEntity<?> processed(@PathVariable("identifier") String identifier) {
        try {
            if (identifier == null || identifier.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            PizzaOrder pizzaOrder = pizzaOrderService.getByIdentifier(identifier);

            if (pizzaOrder == null) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                WorkingProgressStatus statusTMP = pizzaOrder.getStatus();
                if (pizzaOrder.getStatus().equals(WorkingProgressStatus.TAKE_CHARGE))
                    pizzaOrder.setStatus(WorkingProgressStatus.PROCESSED);

                Map<String, Object> map;
                boolean saved = pizzaOrderService.save(pizzaOrder);
                if (saved) {
                    map = pizzaOrderManager.generateMapForProcessed(pizzaOrder, statusTMP);
                } else {
                    map = new LinkedHashMap<>();
                    map.put("Result", "KO");
                    map.put("Description", "The order was not taking charge due to technical problems");
                }
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}