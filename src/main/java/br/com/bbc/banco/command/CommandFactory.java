package br.com.bbc.banco.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandFactory {

    @Autowired
    private CriarCommand criarCommand;

    @Autowired
    private SaldoCommand saldoCommand;

    @Autowired
    private ConviteCommand conviteCommand;

    @Autowired
    private TransferirCommand transferirCommand;

    @Autowired
    private ExtratoCommand extratoCommand;

    @Autowired
    private DailyCommand dailyCommand;

    @Autowired
    private CriarApostaCommand criarApostaCommand;

    @Autowired
    private ApostasCommand apostasCommand;

    @Autowired
    private ApostarCommand apostarCommand;

    @Autowired
    private ApostaCommand apostaCommand;

    @Autowired
    private FinalizarApostaCommand finalizarApostaCommand;

    @Autowired
    private CancelaAposta cancelaAposta;

    @Autowired
    private JokenpoCommand jokenpoCommand;

    @Autowired
    private AceitaJokenpoCommand aceitaJokenpoCommand;

    @Autowired
    private RecusaJokenpoCommand recusaJokenpoCommand;

    @Autowired
    private OpcaoJokenpoCommand opcaoJokenpoCommand;

    public Command factory(String command) throws Exception {
        switch (command){
            case "convite":
                return this.conviteCommand;

            case "saldo":
                return this.saldoCommand;

            case "criar":
                return this.criarCommand;

            case "transferir":
                return this.transferirCommand;

            case "daily":
                return this.dailyCommand;

            case "extrato":
                return this.extratoCommand;

            case "criaraposta":
                return this.criarApostaCommand;

            case "apostas":
                return this.apostasCommand;

            case "apostar":
                return this.apostarCommand;

            case "aposta":
                return this.apostaCommand;

            case "finalizar":
                return this.finalizarApostaCommand;

            case "cancelar":
                return this.cancelaAposta;

            case "jokenpo":
                return this.jokenpoCommand;

            default:
                throw new Exception();
        }
    }

    public Command factoryButton(String command) throws Exception {
        switch (command){
            case "aceitarJokenpo":
                return aceitaJokenpoCommand;
            case "recusarJokenpo":
                return recusaJokenpoCommand;
            case "U+270A":
            case "U+270B":
            case "U+270C":
                return opcaoJokenpoCommand;
            default:
                throw new Exception();
        }
    }
}
