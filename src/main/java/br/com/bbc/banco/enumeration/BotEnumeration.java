package br.com.bbc.banco.enumeration;

public enum BotEnumeration {

    PREFIX("$"),
    TOKEN(System.getenv("TOKEN")),
    CURRENCY("BC$");

    private String value;

    BotEnumeration(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
