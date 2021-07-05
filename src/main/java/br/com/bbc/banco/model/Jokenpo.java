package br.com.bbc.banco.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "jokenpo")
public class Jokenpo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private LocalDateTime startDate = LocalDateTime.now();
    private long player1Id;
    private String player1Pick;
    private long player2Id;
    private String player2Pick;
    private BigDecimal value;
}
