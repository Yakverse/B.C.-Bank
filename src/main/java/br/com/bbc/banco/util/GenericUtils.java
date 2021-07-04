package br.com.bbc.banco.util;

import java.math.BigDecimal;

public class GenericUtils {

    public static BigDecimal convertStringToBigDecimalReplacingComma(String string){
        string = string.replace(',','.');
        return new BigDecimal(string);
    }
}
