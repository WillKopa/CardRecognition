package com.WillKopa.CardIdentifier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTCGResponse {
    private List<CardData> data;
    private int page;

    @lombok.Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CardData {
        private String id;
        private String name;
        private CardSet set;
        private CardImage images;
//        private CardMarket cardmarket; // EU Market prices
        private TcgPlayerMarket tcgplayer; // NA Market prices
    }

    @lombok.Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CardSet {
        private String id;
        private String name;
        private String series;
    }

    @lombok.Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CardImage {
        private String large;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TcgPlayerMarket {
        private String url;
        private String updatedAt;
        private TcgPlayerMarketPriceTypes prices;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TcgPlayerMarketPriceTypes {
        private TcgPlayerMarketPrices normal;
        private TcgPlayerMarketPrices holofoil;
        @JsonProperty("1stEditionHolofoil")
        private TcgPlayerMarketPrices firstEditionHolofoil;
        private TcgPlayerMarketPrices reverseHolofoil;
        @JsonProperty("1stEditionNormal")
        private TcgPlayerMarketPrices firstEditionNormal;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TcgPlayerMarketPrices {
        private BigDecimal low;
        private BigDecimal mid;
        private BigDecimal high;
        private BigDecimal market;
        private BigDecimal directLow;
    }

//    @Data
//    @NoArgsConstructor
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    private static class CardMarket {
//        private String url;
//        private String updatedAt;
//        private CardMarketPrices prices;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class CardMarketPrices {
//        private BigDecimal averageSellPrice;
//        private BigDecimal lowPrice;
//        private BigDecimal trendPrice;
//        private BigDecimal avg1;
//        private BigDecimal avg7;
//        private BigDecimal avg30;
//    }
}
