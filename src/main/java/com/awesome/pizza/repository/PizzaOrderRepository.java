package com.awesome.pizza.repository;

import com.awesome.pizza.model.PizzaOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PizzaOrderRepository extends JpaRepository<PizzaOrder, Integer> {
    Optional<PizzaOrder> findByUuid(String uuid);

    Optional<PizzaOrder> findByIdentifier(String identifier);

    @Query("SELECT w FROM PizzaOrder w WHERE w.status = 1 ORDER BY w.creationTimestamp ASC")
    List<PizzaOrder> findOldPending();
}
