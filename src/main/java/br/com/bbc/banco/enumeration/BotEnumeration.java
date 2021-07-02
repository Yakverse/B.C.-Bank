package br.com.bbc.banco.enumeration;

public enum BotEnumeration {

    PREFIX("$"),
    TOKEN("ODI2NTc3NDQwNTQ5NTAyOTc2.YGOgOg.Z_km4PHB7WA4rlhu7HuLUUoMt70");

    private String value;

    BotEnumeration(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
