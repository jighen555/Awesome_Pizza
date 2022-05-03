package com.awesome.pizza.model;

import com.awesome.pizza.constants.WorkingProgressStatus;
import com.awesome.pizza.converter.PizzaOrderConverter;
import com.awesome.pizza.converter.WorkingProgressStatusConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pizza_order")
public class PizzaOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "status")
    @Convert(converter = WorkingProgressStatusConverter.class)
    private WorkingProgressStatus status;

    @Column
    @Convert(converter = PizzaOrderConverter.class)
    private Set<String> pizzas;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Date creationTimestamp;
}
