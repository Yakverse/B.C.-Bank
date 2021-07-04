package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "user_bet")
public class UserBet {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;
}
