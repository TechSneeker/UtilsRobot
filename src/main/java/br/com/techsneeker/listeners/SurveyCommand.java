package br.com.techsneeker.listeners;

import br.com.techsneeker.objects.Survey;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SurveyCommand extends ListenerAdapter {

    final List<Survey> surveyRegistered = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("survey")) {
            event.deferReply().queue();

            final String owner = event.getUser().getName();
            final String selectId = this.generateRandomId();
            final MessageChannel channel = event.getChannel();
            final String unitInserted = Objects.requireNonNull(event.getOption("unit")).getAsString();
            final String choicesInserted = Objects.requireNonNull(event.getOption("choices")).getAsString();
            final String questionInserted = Objects.requireNonNull(event.getOption("question")).getAsString();
            final long durationInserted = Objects.requireNonNull(event.getOption("duration")).getAsLong();

            List<String> choices = this.extractChoicesBySurvey(choicesInserted);
            List<SelectOption> options = this.createSelectOptions(choices);

            StringSelectMenu selectMenu = StringSelectMenu.create(selectId)
                    .addOptions(options)
                    .build();

            EmbedBuilder embed = new EmbedBuilder();

            for (SelectOption option : options) {
                String defaultProgressBar = String.format("`%s` 0 - 0", "⬛".repeat(10)) + "%";
                embed.addField(option.getValue(), defaultProgressBar, false);
            }

            embed.setTitle(questionInserted).setColor(Color.decode("#FFFF00"));

            MessageCreateBuilder builder = new MessageCreateBuilder()
                    .addEmbeds(embed.build())
                    .addActionRow(selectMenu);

            ActionRow actionRow = ActionRow.of(selectMenu.asDisabled());

            event.getHook().sendMessage(builder.build()).queue((message) -> {
                surveyRegistered.add(this.createSurvey(selectId, owner, options));

                channel.editMessageComponentsById(message.getId(), actionRow).queueAfter(durationInserted, TimeUnit.valueOf(unitInserted), (sucess) -> {
                            Survey.removeFromListById(surveyRegistered, selectId);
                });
            });

        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        final User user = event.getUser();
        final String componentId = event.getInteraction().getComponentId();
        final String optSelected = event.getSelectedOptions().get(0).getValue();
        final Survey surveyFound = Survey.getFromListById(surveyRegistered, UUID.fromString(componentId));
        final List<MessageEmbed.Field> fields = event.getMessage().getEmbeds().get(0).getFields();

        if (surveyFound.hasVoted(user.getId())) {
            event.reply("You have already voted!").setEphemeral(true).queue();
            return;
        }

        surveyFound.addVoter(user.getId());
        surveyFound.addVote(optSelected);

        List<MessageEmbed.Field> newFields = new ArrayList<>();

        for (MessageEmbed.Field field : fields) {
            String fieldName = field.getName();
            int total = surveyFound.getVoterCount();
            int count = surveyFound.getVote().get(fieldName);
            double percentage = (double) count / total * 100;

            newFields.add(this.createUpdatedField(fieldName, count, percentage));
        }

        String id = event.getMessageId();
        MessageChannel channel = event.getChannel();
        MessageEmbed embed = event.getMessage().getEmbeds().get(0);
        EmbedBuilder builder = EmbedBuilder.fromData(embed.toData()).clearFields();

        newFields.forEach(builder::addField);

        channel.editMessageEmbedsById(id, builder.build()).queue();
        event.reply(String.format("You voted for %s!", optSelected)).setEphemeral(true).queue();
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

    private Survey createSurvey(String id, String owner, List<SelectOption> options) {
        Map<String, Integer> votes = new HashMap<>();

        options.forEach(option -> {
            votes.put(option.getValue(), 0);
        });

        return new Survey(id, owner, votes);
    }

    private MessageEmbed.Field createUpdatedField(String fieldName, int count, double percentage) {
        String progressBar = this.createProgressBar(count, percentage);
        return new MessageEmbed.Field(fieldName, progressBar, false);
    }

    private String createProgressBar(int count, double percentage) {
        final int totalSquares = 10;
        final int greenSquareCount = (int) Math.min(Math.round(percentage / 10), 10);
        final int blackSquareCount = totalSquares - greenSquareCount;

        String countAndPorcent = String.format(" %d - %.1f", count, percentage) + "%";
        String progressBar = "\uD83D\uDFE9".repeat(greenSquareCount) + "⬛".repeat(blackSquareCount);

        return String.format("`%s`", progressBar) + countAndPorcent;
    }

    public List<OptionData> getOptions() {
        return Arrays.asList(
                new OptionData(OptionType.STRING, "question", "survey question")
                        .setRequired(true),
                new OptionData(OptionType.STRING, "choices", "survey choices")
                        .setRequired(true),
                new OptionData(OptionType.STRING, "unit", "unit of duration")
                        .addChoices(this.getTimeUnitOptions())
                        .setRequired(true),
                new OptionData(OptionType.STRING, "duration", "survey duration")
                        .setRequired(true)
        );
    }

    private List<Command.Choice> getTimeUnitOptions() {
        return Arrays.asList(
                new Command.Choice("Seconds", "SECONDS"),
                new Command.Choice("Minutes", "MINUTES"),
                new Command.Choice("Hours", "HOURS"),
                new Command.Choice("Days", "DAYS")
        );
    }

    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }

}
