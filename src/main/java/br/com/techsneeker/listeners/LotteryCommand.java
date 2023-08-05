package br.com.techsneeker.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LotteryCommand extends ListenerAdapter {

    private static final String resultAnwser = "\uD83C\uDF89\tCongratulations <mention>! You're the winner\t\uD83C\uDF89";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("lottery")) {
            event.deferReply().queue();

            String textContent = event.getOption("participants").getAsString();
            List<User> participants = this.extractMentionedUsers(textContent, event);

            User winner = this.getRandomUser(participants);
            String mention = String.format("<@%s>", winner.getId());
            String result = resultAnwser.replace("<mention>", mention);

            event.getHook().editOriginal(result).queue();
        }
    }

    private List<User> extractMentionedUsers(String text, SlashCommandInteractionEvent event) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(text);

        List<String> mentionedUsersIds = new ArrayList<>();

        while (matcher.find())
            mentionedUsersIds.add(matcher.group());

        return mentionedUsersIds.stream().map(id -> event.getJDA().retrieveUserById(id).complete())
                .collect(Collectors.toList());
    }

    private User getRandomUser(List<User> participants) {
        return (!participants.isEmpty())
                ? participants.get(ThreadLocalRandom.current().nextInt(participants.size()))
                : null;
    }

    public List<OptionData> getOptions() {
        return Arrays.asList(
                new OptionData(OptionType.STRING, "participants", "who'll participate?")
                        .setRequired(true)
        );
    }
}
