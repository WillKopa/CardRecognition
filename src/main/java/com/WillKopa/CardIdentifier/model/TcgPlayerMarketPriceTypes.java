package com.WillKopa.CardIdentifier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcgPlayerMarketPriceTypes {
    private TcgPlayerMarketPrices normal;
    private TcgPlayerMarketPrices holofoil;
    @JsonProperty("1stEditionHolofoil")
    private TcgPlayerMarketPrices firstEditionHolofoil;
    private TcgPlayerMarketPrices reverseHolofoil;
    @JsonProperty("1stEditionNormal")
    private TcgPlayerMarketPrices firstEditionNormal;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TcgPlayerMarketPrices {
        private BigDecimal low;
        private BigDecimal mid;
        private BigDecimal high;
        private BigDecimal market;
        private BigDecimal directLow;
    }
}
