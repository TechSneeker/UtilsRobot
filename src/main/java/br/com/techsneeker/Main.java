package br.com.techsneeker;

import br.com.techsneeker.envs.Environment;
import br.com.techsneeker.listeners.LotteryCommand;
import br.com.techsneeker.listeners.TranslationCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {

        Environment variables = new Environment();

        JDA utilsRobot = JDABuilder.createDefault(variables.getDiscordToken()) // Start robot
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        TranslationCommand translation = new TranslationCommand();

        utilsRobot.addEventListener(translation); // Add event
        utilsRobot.upsertCommand("translate", "Translate texts") // Create command
                .addOptions(translation.getOptions()).queue();

        LotteryCommand lottery = new LotteryCommand();

        utilsRobot.addEventListener(lottery); // Add event
        utilsRobot.upsertCommand("lottery", "Choose one") // Create command
                .addOption(OptionType.STRING, "participants", "who'll participate?").queue();

    }
}