package com.monetovani.monetovanisrv.entity;

import javax.persistence.*;
import java.util.List;

class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    private String name;

    @OneToMany(mappedBy = "goal")
    private List<GoalType> type; // TODO: I need to test the behavior of this foreign key. Whether GoalMarks are deleted when Goal is deleted

    public enum GoalType {
        BALANCE,
        EARNINGS,
        EXPENSES
    }
}
