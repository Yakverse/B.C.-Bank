package br.com.bbc.banco.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
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
    private String player1Pick;
    private long player2Id;
    private String player2Pick;
    private BigDecimal value;

    public int winner(){
        String pick1 = this.getPlayer1Pick();
        String pick2 = this.getPlayer2Pick();

        System.out.println(pick1);
        System.out.println(pick2);
        if(pick1.equals(pick2)) return 0;
        switch (pick1){
            case "U+270A":
                if(pick2.equals("U+270B")) return 2;
                return 1;
            case "U+270B":
                if(pick2.equals("U+270C")) return 2;
                return 1;
            case "U+270C":
                if(pick2.equals("U+270A")) return 2;
                return 1;
            default:
                throw new IllegalStateException("Unexpected value: " + pick1);
        }
    }


}
