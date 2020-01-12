package com.monetovani.monetovanisrv.entity.financial;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String name;

    @OneToMany(mappedBy = "goal")
    private List<GoalMarks> marks;

    @Enumerated(EnumType.STRING)
    private GoalType type; // TODO: I need to test the behavior of this foreign key. Whether GoalMarks are deleted when Goal is deleted

    public enum GoalType {
        BALANCE,
        TOTAL_EARNINGS,
        TOTAL_EXPENSES,
        CATEGORY_EARNINGS,
        CATEGORY_EXPENSES
    }
}
