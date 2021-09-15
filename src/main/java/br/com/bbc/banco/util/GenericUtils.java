package br.com.bbc.banco.util;

import br.com.bbc.banco.exception.ValorInvalidoException;

import java.math.BigDecimal;

public class GenericUtils {

    public static void asciibbc(){
        System.out.println(" /$$$$$$$                                            /$$$$$$$                         /$$                      /$$                 /$$$$$$ /$$                        /$$                  ");
        System.out.println("| $$__  $$                                          | $$__  $$                       | $$                     | $$                /$$__  $| $$                       | $$                  ");
        System.out.println("| $$  \\ $$ /$$$$$$ /$$$$$$$  /$$$$$$$ /$$$$$$       | $$  \\ $$/$$   /$$/$$$$$$$  /$$$$$$$ /$$$$$$         /$$$$$$$ /$$$$$$       | $$  \\__| $$$$$$$  /$$$$$$  /$$$$$$| $$ /$$$$$$  /$$$$$$ ");
        System.out.println("| $$$$$$$ |____  $| $$__  $$/$$_____//$$__  $$      | $$$$$$$| $$  | $| $$__  $$/$$__  $$|____  $$       /$$__  $$/$$__  $$      | $$     | $$__  $$|____  $$/$$__  $| $$|____  $$/$$__  $$");
        System.out.println("| $$__  $$ /$$$$$$| $$  \\ $| $$     | $$  \\ $$      | $$__  $| $$  | $| $$  \\ $| $$  | $$ /$$$$$$$      | $$  | $| $$  \\ $$      | $$     | $$  \\ $$ /$$$$$$| $$  \\__| $$ /$$$$$$| $$  \\ $$");
        System.out.println("| $$  \\ $$/$$__  $| $$  | $| $$     | $$  | $$      | $$  \\ $| $$  | $| $$  | $| $$  | $$/$$__  $$      | $$  | $| $$  | $$      | $$    $| $$  | $$/$$__  $| $$     | $$/$$__  $| $$  | $$");
        System.out.println("| $$$$$$$|  $$$$$$| $$  | $|  $$$$$$|  $$$$$$/      | $$$$$$$|  $$$$$$| $$  | $|  $$$$$$|  $$$$$$$      |  $$$$$$|  $$$$$$/      |  $$$$$$| $$  | $|  $$$$$$| $$     | $|  $$$$$$|  $$$$$$/");
        System.out.println("|_______/ \\_______|__/  |__/\\_______/\\______/       |_______/ \\______/|__/  |__/\\_______/\\_______/       \\_______/\\______/        \\______/|__/  |__/\\_______|__/     |__/\\_______/\\______/ ");
    }

    public static BigDecimal convertStringToBigDecimalReplacingComma(String string) throws ValorInvalidoException {
        string = string.replace(',','.');
        try{
            return new BigDecimal(string);
        } catch (Exception e){
            throw new ValorInvalidoException();
        }
    }
}
