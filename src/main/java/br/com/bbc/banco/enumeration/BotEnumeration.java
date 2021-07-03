package br.com.bbc.banco.enumeration;

public enum BotEnumeration {

    PREFIX("$"),
    TOKEN("Njc5MTUzNzU0MTc1NzAxMDMy.XktNOQ.iaFHfe8Xcr-Eo6e9v3tJJWSehb0"),
    CURRENCY("BC$");

    private String value;

    BotEnumeration(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
