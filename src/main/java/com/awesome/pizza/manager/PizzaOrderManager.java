package com.awesome.pizza.manager;

import com.awesome.pizza.constants.PizzaOrderConstant;
import com.awesome.pizza.constants.ResultStatus;
import com.awesome.pizza.constants.WorkingProgressStatus;
import com.awesome.pizza.dto.PizzaOrderDTO;
import com.awesome.pizza.model.Pizza;
import com.awesome.pizza.model.PizzaOrder;
import com.awesome.pizza.service.PizzaService;
import com.awesome.pizza.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PizzaOrderManager {

    @Autowired
    private PizzaService pizzaService;

    /**
     * Method for generating a WorkingProgress object by a PizzaOrder object.
     * @param pizzasMap: PizzaOrder object
     * @param status: WorkingProgress object status
     * @return WorkingProgress generated object
     */
    public PizzaOrder generatePizzaOrder(Map<String, List<String>> pizzasMap, WorkingProgressStatus status) {
        List<String> pizzas = pizzasMap.get("pizzas");
        if (pizzas == null || pizzas.isEmpty())
            return null;

        for (String pizzaString : pizzas) {
            Pizza pizza = pizzaService.getByUuid(pizzaString);
            if (pizza == null)
                return null;
        }

        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.setUuid(UUID.randomUUID().toString());
        pizzaOrder.setIdentifier(StringUtils.generateSecureRandomString());
        pizzaOrder.setPizzas(new HashSet<>(pizzas));
        pizzaOrder.setStatus(status);
        pizzaOrder.setCreationTimestamp(new Date());

        return pizzaOrder;
    }

    /**
     * Method for generating a WorkingProgress object by a PizzaOrder object.
     * The WorkingProgress object will have PENDING status.
     * @param pizzasMap: PizzaOrder object
     * @return WorkingProgress object
     */
    public PizzaOrder generatePizzaOrderPenging(Map<String, List<String>> pizzasMap) {
        return generatePizzaOrder(pizzasMap, WorkingProgressStatus.PENDING);
    }

    /**
     * Method for generating a List of PizzaOrderDTO by pizzaOrder object and and its associated pizzas.
     * @param pizzaOrders: PizzaOrder object
     * @param allPizzas: List of all Pizzas in Database
     * @return List of PizzaOrderDTO object
     */
    public List<PizzaOrderDTO> fillPizzas(List<PizzaOrder> pizzaOrders, List<Pizza> allPizzas) {
        List<PizzaOrderDTO> pizzaOrderDTOS = new ArrayList<>();

        if (pizzaOrders != null && !pizzaOrders.isEmpty()) {
            for (PizzaOrder pizzaOrder : pizzaOrders) {
                Set<String> pizzaUuidsSet = pizzaOrder.getPizzas();
                List<Pizza> pizzas = getPizzas(pizzaUuidsSet, allPizzas);
                pizzaOrderDTOS.add(PizzaOrderDTO.build(pizzaOrder, pizzas));
            }
        }

        return pizzaOrderDTOS;
    }

    /**
     * Method that returns the Pizza objects given by the input uuids.
     * @param pizzaUuidsSet: Set of Pizza's uuids
     * @param pizzas: List of all Pizzas in Database
     * @return List of Pizza objects
     */
    public List<Pizza> getPizzas(Set<String> pizzaUuidsSet, List<Pizza> pizzas) {
        List<Pizza> pizzaList = new ArrayList<>();
        for (Pizza pizza : pizzas) {
            for (String s : pizzaUuidsSet) {
                if (pizza.getUuid().equals(s))
                    pizzaList.add(pizza);
            }
        }

        return pizzaList;
    }

    /**
     * Method of generating a map containing the PizzaOrder object
     * @param pizzaOrder: PizzaOrder object
     * @return Map
     */
    public Map<String, Object> generateMapForStatus(PizzaOrder pizzaOrder) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (pizzaOrder == null) {
            map.put("Result", ResultStatus.KO.toString());
            map.put("Description", PizzaOrderConstant.PIZZA_ORDER_NULL.getDescription());
            map.put("Status", null);
        } else {
            map.put("Result", ResultStatus.OK.toString());
            map.put("Description", PizzaOrderConstant.PIZZA_ORDER_OK.getDescription());
            map.put("Status", pizzaOrder.getStatus());
        }

        return map;
    }

    /**
     * Method of generating a map containing the oldest pending PizzaOrder object
     * @param pizzaOrder: PizzaOrder object
     * @return Map
     */
    public Map<String, Object> generateMapForOldestPending(PizzaOrder pizzaOrder) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (pizzaOrder == null) {
            map.put("Result", ResultStatus.KO.toString());
            map.put("Identifier", null);
            map.put("Description", PizzaOrderConstant.NO_OLDEST_PENDING.getDescription());
        } else {
            map.put("Result", ResultStatus.OK);
            map.put("Identifier", pizzaOrder.getIdentifier());
            map.put("Description", PizzaOrderConstant.OLDEST_PENDING.getDescription());
        }

        return map;
    }

    /**
     * Method of generating a map containing the take chage event
     * @param pizzaOrder: PizzaOrder object
     * @param statusTMP: WorkingProgressStatus before taking charge
     * @return Map
     */
    public Map<String, Object> generateMapForTakeCharge(PizzaOrder pizzaOrder,
                                                        WorkingProgressStatus statusTMP) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (pizzaOrder == null) {
            map.put("Result", "KO");
            map.put("Description", "The WorkingProgress object is null");
        } else {
            if (statusTMP.equals(WorkingProgressStatus.PENDING)) {
                if (pizzaOrder.getStatus().equals(WorkingProgressStatus.PENDING)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ERROR_TAKING_CHARGE.getDescription());
                } else if (pizzaOrder.getStatus().equals(WorkingProgressStatus.TAKE_CHARGE)) {
                    map.put("Result", ResultStatus.OK.toString());
                    map.put("Description", PizzaOrderConstant.SUCCESS_TAKING_CHARGE.getDescription());
                } else if (pizzaOrder.getStatus().equals(WorkingProgressStatus.PROCESSED)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ERROR_TAKING_CHARGE.getDescription());
                }
            } else {
                if (statusTMP.equals(WorkingProgressStatus.TAKE_CHARGE)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ALREADY_TAKING_CHARGE.getDescription());
                } else if (statusTMP.equals(WorkingProgressStatus.PROCESSED)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ALREADY_PROCESSED.getDescription());
                }
            }
        }

        return map;
    }

    /**
     * Method of generating a map containing the processed event
     * @param pizzaOrder: PizzaOrder object
     * @param statusTMP: WorkingProgressStatus before taking charge
     * @return Map
     */
    public Map<String, Object> generateMapForProcessed(PizzaOrder pizzaOrder,
                                                       WorkingProgressStatus statusTMP) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (pizzaOrder == null) {
            map.put("Result", ResultStatus.KO.toString());
            map.put("Description", "The WorkingProgress object is null");
        } else {
            if (statusTMP.equals(WorkingProgressStatus.TAKE_CHARGE)) {
                if (pizzaOrder.getStatus().equals(WorkingProgressStatus.PENDING)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ERROR_PROCESSED.getDescription());
                } else if (pizzaOrder.getStatus().equals(WorkingProgressStatus.TAKE_CHARGE)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ERROR_PROCESSED.getDescription());
                } else if (pizzaOrder.getStatus().equals(WorkingProgressStatus.PROCESSED)) {
                    map.put("Result", ResultStatus.OK.toString());
                    map.put("Description", PizzaOrderConstant.SUCCESS_PROCESSED.getDescription());
                }
            } else {
                if (statusTMP.equals(WorkingProgressStatus.PENDING)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.STILL_PENDING.getDescription());
                } else if (statusTMP.equals(WorkingProgressStatus.PROCESSED)) {
                    map.put("Result", ResultStatus.KO.toString());
                    map.put("Description", PizzaOrderConstant.ALREADY_PROCESSED.getDescription());
                }
            }
        }

        return map;
    }
}
