package br.com.bbc.banco;

import br.com.bbc.banco.event.EventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BotConfiguration {

    private static final Logger log = LoggerFactory.getLogger( BotConfiguration.class );

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = null;
        try {
            client = DiscordClientBuilder.create("ODI2NTc3NDQwNTQ5NTAyOTc2.YGOgOg.Z_km4PHB7WA4rlhu7HuLUUoMt70")
                    .build()
                    .login()
                    .block();

            client.getEventDispatcher().on(ReadyEvent.class)
                    .subscribe(event -> {
                        final User self = event.getSelf();
                        System.out.println(String.format(
                                "Bot inicializado como %s", self.getTag()
                        ));
                    });

            for(EventListener<T> listener : eventListeners) {
                client.on(listener.getEventType())
                        .flatMap(listener::execute)
                        .onErrorResume(listener::handleError)
                        .subscribe();
            }

            client.onDisconnect().block();
        } catch (Exception exception){
            log.error( "Token inválido!", exception );
        }

        return client;
    }
}
