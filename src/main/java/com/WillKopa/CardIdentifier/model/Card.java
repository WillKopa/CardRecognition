package com.WillKopa.CardIdentifier.model;

import com.WillKopa.CardIdentifier.converter.VectorConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String externalDbId;
    private String game;
    private String name;
    private String cardSet;
    private String cardSetId;

    @Convert(converter = VectorConverter.class)
    @Column(name = "image_embedding", columnDefinition = "vector(1000)")
    private float[] imageEmbedding;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private TcgPlayerMarketPriceTypes priceTypes;
    private Date lastUpdate;
}
