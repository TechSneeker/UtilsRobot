package br.com.techsneeker;

import br.com.techsneeker.database.Database;
import br.com.techsneeker.envs.Environment;
import br.com.techsneeker.listeners.ConfigCommand;
import br.com.techsneeker.listeners.LotteryCommand;
import br.com.techsneeker.listeners.SurveyCommand;
import br.com.techsneeker.listeners.TranslationCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    private static JDA utilsRobot;
    private static Database db;

    static {

        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("logging.properties")){
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            db = new Database();
            db.createTables();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Environment variables = new Environment();

        TranslationCommand translation = new TranslationCommand();
        LotteryCommand lottery = new LotteryCommand();
        SurveyCommand survey = new SurveyCommand();
        ConfigCommand config = new ConfigCommand();

        utilsRobot = JDABuilder.createDefault(variables.getDiscordToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(translation, lottery, survey, config)
                .build();

        utilsRobot.upsertCommand("translate", "Translate texts").addOptions(translation.getOptions()).queue();
        utilsRobot.upsertCommand("lottery", "Choose one").addOptions(lottery.getOptions()).queue();
        utilsRobot.upsertCommand("survey", "Ask something").addOptions(survey.getOptions()).queue();
        utilsRobot.upsertCommand("survey-permissions", "Choose usage permissions").addOptions(config.getOptions()).queue();
    }

    public static Database getDatabaseInstance() {
        return db;
    }

}