package br.com.techsneeker.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class Utils {

    public static boolean isOwner(SlashCommandInteractionEvent event) {
        long ownerId = Objects.requireNonNull(event.getGuild()).getOwnerIdLong();
        long userId = Objects.requireNonNull(event.getUser()).getIdLong();

        return (userId == ownerId);
    }

}
