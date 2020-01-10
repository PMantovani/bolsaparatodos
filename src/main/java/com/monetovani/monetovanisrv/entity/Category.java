package com.monetovani.monetovanisrv.entity;

import javax.persistence.*;

class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id")
    private Category parent;

    private String name;

}
