package br.com.bbc.banco.model;

import lombok.Getter;
import lombok.Setter;

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

    @OneToMany(mappedBy = "bet", cascade = CascadeType.ALL)
    private List<Option> options;

    @OneToOne
    private User createdBy;
}
