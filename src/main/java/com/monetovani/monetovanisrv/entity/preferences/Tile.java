package com.monetovani.monetovanisrv.entity.preferences;

import com.monetovani.monetovanisrv.entity.financial.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
class Tile {

    @Id
    private Long id;

    @ManyToOne
    private DashboardLayout dashboard;

    private TileType type;

    public enum TileType {
        BALANCE_CHART,          // Line, Bar or Area Chart with evolution of balance over time
        // (variables: start/end date, period, indexes, account, chart type)
        EXPENSES_INCOME_CHART,  // Line, Bar or Area chart with expenses and income (variables: start/end date, show goal, account)
        CATEGORY_CHART,         // Pie or radar chart with distribution of money (variables: expenses or incomes, with goal)
        POSITION_VALUE,         // Text card with value in position and appreciation since purchase in month
        // (or until purchase if purchase was done in this month)
        BALANCE,                // Text card with full balance and month appreciation (variables: account)

    }
}
