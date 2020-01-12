package com.monetovani.monetovanisrv.entity.preferences;

import com.monetovani.monetovanisrv.entity.financial.User;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
class DashboardLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "dashboard")
    private List<Tile> tiles;

}
