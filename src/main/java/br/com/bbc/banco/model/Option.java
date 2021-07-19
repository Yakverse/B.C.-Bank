package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "option")
public class Option {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String text;
    private boolean winner;
    private int number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bet_id")
    private Bet bet;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    private List<UserBet> user_bet;
}
