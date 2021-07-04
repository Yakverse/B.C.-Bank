package br.com.bbc.banco.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    private long player2Id;
    private long value;
    private boolean hasStarted = false;
    private long confirmationMessageId;
}
