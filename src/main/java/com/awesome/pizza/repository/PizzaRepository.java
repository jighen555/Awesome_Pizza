package com.awesome.pizza.repository;

import com.awesome.pizza.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    Optional<Pizza> findByUuid(String uuid);
}
