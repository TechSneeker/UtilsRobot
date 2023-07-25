package br.com.techsneeker.listeners;

import br.com.techsneeker.objects.Survey;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SurveyCommand extends ListenerAdapter {

    List<Survey> surveyRegistered = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("survey")) {
            event.deferReply().queue();

            final UUID id = UUID.randomUUID();
            final String owner = event.getUser().getName();
            final String choicesInserted = event.getOption("choices").getAsString();
            final String questionInserted = event.getOption("question").getAsString();

            List<String> choices = this.extractChoicesBySurvey(choicesInserted);
            List<SelectOption> options = this.createSelectOptions(choices);

            StringSelectMenu selectMenu = StringSelectMenu.create(id.toString())
                    .addOptions(options)
                    .build();

            MessageCreateBuilder builder = new MessageCreateBuilder()
                    .addContent(questionInserted)
                    .addActionRow(selectMenu);

            event.getHook().sendMessage(builder.build()).queue((sucess) ->
                    surveyRegistered.add(this.createSurvey(id, owner, options)));
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        final String componentId = event.getInteraction().getComponentId();
        final Survey surveyFound = Survey.fromListById(surveyRegistered, UUID.fromString(componentId));
        final String optSelected = event.getSelectedOptions().get(0).getValue();

        surveyFound.addVote(optSelected);
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

    private Survey createSurvey(UUID id, String owner, List<SelectOption> options) {
        Map<String, Integer> votes = new HashMap<>();

        options.stream().forEach(option -> {
            votes.put(option.getValue(), 0);
        });

        return new Survey(id, owner, votes);
    }

}
