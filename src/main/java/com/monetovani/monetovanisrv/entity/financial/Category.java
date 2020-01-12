package com.monetovani.monetovanisrv.entity.financial;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Category parent;

    private String name;

}
