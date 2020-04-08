package com.bolsaparatodos.bolsaparatodos.entity.financial.keys;

import com.bolsaparatodos.bolsaparatodos.entity.financial.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MarketDataKeys implements Serializable {

    @ManyToOne
    Asset asset;
    LocalDate eventDate;

}
