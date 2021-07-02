package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private long id;

    private LocalDateTime date = LocalDateTime.now();

    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "origin_user_id")
    private User originUser;

}
