package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
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

    @OneToMany(mappedBy = "user")
    private List<Transaction> transaction;
}
