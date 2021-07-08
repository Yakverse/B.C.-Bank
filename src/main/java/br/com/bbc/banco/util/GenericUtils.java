package br.com.bbc.banco.util;

import br.com.bbc.banco.exception.ValorInvalidoException;

import java.math.BigDecimal;

public class GenericUtils {

    public static BigDecimal convertStringToBigDecimalReplacingComma(String string) throws ValorInvalidoException {
        string = string.replace(',','.');
        try{
            return new BigDecimal(string);
        } catch (Exception e){
            throw new ValorInvalidoException();
        }
    }
}
