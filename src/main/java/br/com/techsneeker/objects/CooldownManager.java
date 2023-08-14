package br.com.techsneeker.objects;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private static final Cache<Long, Long> permCache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS).build();

    private static final Cache<Long, Long> surveyCache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS).build();

    public static boolean canUsePermissionCommand(long id, SlashCommandInteractionEvent event) {
        return isCachedUser(permCache.getIfPresent(id), event);
    }

    public static boolean canUseSurveyCommand(long id, SlashCommandInteractionEvent event) {
        return isCachedUser(surveyCache.getIfPresent(id), event);
    }

    public static void addToPermCache(long id) {
        permCache.put(id, System.currentTimeMillis());
    }

    public static void addToSurveyCache(long id) {
        surveyCache.put(id, System.currentTimeMillis());
    }

    private static boolean isCachedUser(Long lastUsage, SlashCommandInteractionEvent event) {

        if (lastUsage != null) {
            long remainingCooldown = TimeUnit.SECONDS.toMillis(15) - (System.currentTimeMillis() - lastUsage);
            String cooldownAwnser = String.format("Please, wait %d seconds to use this command again!",
                    remainingCooldown / 1000);

            event.reply(cooldownAwnser).setEphemeral(true).queue();
            return false;
        }

        return true;
    }

}
