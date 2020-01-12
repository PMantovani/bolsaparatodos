package com.monetovani.monetovanisrv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Balance {

    private float balance;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;

}
