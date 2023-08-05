package br.com.techsneeker;

import br.com.techsneeker.database.Database;
import br.com.techsneeker.envs.Environment;
import br.com.techsneeker.listeners.LotteryCommand;
import br.com.techsneeker.listeners.SurveyCommand;
import br.com.techsneeker.listeners.TranslationCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.net.URISyntaxException;

public class Main {

    private static Database db;

    static {
        try {
            db = new Database();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        Environment variables = new Environment();

        TranslationCommand translation = new TranslationCommand();
        LotteryCommand lottery = new LotteryCommand();
        SurveyCommand survey = new SurveyCommand();

        JDA utilsRobot = JDABuilder.createDefault(variables.getDiscordToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(translation, lottery, survey)
                .build();

        utilsRobot.upsertCommand("translate", "Translate texts").addOptions(translation.getOptions()).queue();
        utilsRobot.upsertCommand("lottery", "Choose one").addOptions(lottery.getOptions()).queue();
        utilsRobot.upsertCommand("survey", "Ask something").addOptions(survey.getOptions()).queue();
    }
}