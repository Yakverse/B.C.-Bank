package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bet")
public class Bet {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String nome;
    private Boolean isOpen = true;
    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "bet")
    private List<Option> options;

    @OneToOne
    private User createdBy;
}
