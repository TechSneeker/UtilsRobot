package br.com.techsneeker;

import br.com.techsneeker.envs.Environment;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {

        Environment variables = new Environment();

        JDA jda = JDABuilder.createDefault(variables.getDiscordToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners()
                .build();
    }
}