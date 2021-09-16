package br.com.bbc.banco.model;

import br.com.bbc.banco.enumeration.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private long id;
    private LocalDateTime date = LocalDateTime.now();
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "origin_user_id")
    private User originUser;

    public Transaction(BigDecimal valor, User de, User para, TransactionType type){
        this.valor = valor;
        this.originUser = de;
        this.user = para;
        this.type = type;
    }

}
