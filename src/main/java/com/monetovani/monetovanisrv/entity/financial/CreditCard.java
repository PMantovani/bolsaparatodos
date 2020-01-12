package com.monetovani.monetovanisrv.entity.financial;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    User user;
    String name;
    String icon;
    LocalDate paymentDate;
}
