package com.monetovani.monetovanisrv.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

public class GoalMarks {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private Goal goal;

    private LocalDateTime realizationDatetime; // When the goal should be accomplished by
    private float value;
}
