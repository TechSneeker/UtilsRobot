package br.com.techsneeker;

import br.com.techsneeker.envs.Environment;
import br.com.techsneeker.listeners.LotteryCommand;
import br.com.techsneeker.listeners.SurveyCommand;
import br.com.techsneeker.listeners.TranslationCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {

        Environment variables = new Environment();

        JDA utilsRobot = JDABuilder.createDefault(variables.getDiscordToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        TranslationCommand translation = new TranslationCommand();

        utilsRobot.addEventListener(translation);
        utilsRobot.upsertCommand("translate", "Translate texts")
                .addOptions(translation.getOptions()).queue();

        LotteryCommand lottery = new LotteryCommand();

        utilsRobot.addEventListener(lottery);
        utilsRobot.upsertCommand("lottery", "Choose one")
                .addOption(OptionType.STRING, "participants", "who'll participate?").queue();

        SurveyCommand survey = new SurveyCommand();

        utilsRobot.addEventListener(survey);
        utilsRobot.upsertCommand("survey", "Ask something")
                .addOption(OptionType.STRING, "question", "survey question", true)
                .addOption(OptionType.STRING, "choices", "survey choices", true)
                .queue();

    }
}