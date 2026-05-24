package com.WillKopa.CardIdentifier.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    public static class CardData {
        private String id;
        private String name;
        private CardSet set;
        private CardImage images;
        private TcgPlayerMarket tcgplayer; // NA Market prices
    }

    @lombok.Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardSet {
        private String id;
        private String name;
        private String series;
    }

    @lombok.Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardImage {
        private String large;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TcgPlayerMarket {
        private String url;

        @JsonFormat(pattern = "yyyy/MM/dd")
        private Date updatedAt;
        private TcgPlayerMarketPriceTypes prices;
    }
}
