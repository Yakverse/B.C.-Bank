package br.com.bbc.banco.enumeration;

public enum BotEnumeration {

    PREFIX("$"),
    TOKEN(System.getenv("TOKEN")),
    CURRENCY("BC$"),
    INVITE_LINK("https://discord.com/api/oauth2/authorize?client_id=826577440549502976&permissions=8&scope=applications.commands%20bot");

    private String value;

    BotEnumeration(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
