package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
    private BigDecimal saldo = new BigDecimal(1000);
    private Boolean isAdmin = false;
    private LocalDateTime ultimoDaily = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    private List<Transaction> transaction;

    @OneToMany(mappedBy = "user")
    private List<UserBet> user_bet;
}
