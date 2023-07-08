package br.com.techsneeker.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LotteryCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("lottery")) {
            event.deferReply().queue();

            String textContent = event.getOption("participants").getAsString();
            List<User> participants = extractMentionedUsers(textContent, event);

            User winner = this.getRandomUser(participants);

            event.getHook().editOriginal(winner.getName()).queue();
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
}
