package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
    private BigDecimal saldo = BigDecimal.ZERO;
}
