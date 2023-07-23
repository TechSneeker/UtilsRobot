package br.com.techsneeker.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SurveyCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("survey")) {
            event.deferReply().queue();

            final String id = UUID.randomUUID().toString();
            final String choicesInserted = event.getOption("choices").getAsString();
            final String questionInserted = event.getOption("question").getAsString();

            List<String> choices = this.extractChoicesBySurvey(choicesInserted);
            List<SelectOption> options = this.createSelectOptions(choices);

            StringSelectMenu selectMenu = StringSelectMenu.create(id)
                    .addOptions(options)
                    .build();

            MessageCreateBuilder builder = new MessageCreateBuilder()
                    .addContent(questionInserted)
                    .addActionRow(selectMenu);

            event.getHook().sendMessage(builder.build()).queue();
        }
    }

    private List<String> extractChoicesBySurvey(String value) {
        Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(value);

        List<String> choices = new ArrayList<>();

        while (matcher.find())
            choices.add(matcher.group(1));

        return choices.stream()
                .filter(choice -> !(choice.isEmpty() || choice.isBlank()))
                .collect(Collectors.toList());
    }

    private List<SelectOption> createSelectOptions(List<String> choices) {
        return choices.stream()
                .map(choice -> SelectOption.of(choice, choice))
                .collect(Collectors.toList());
    }

}
