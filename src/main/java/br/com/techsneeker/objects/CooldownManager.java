package br.com.techsneeker.objects;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private static final Cache<Long, Long> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();

    public static boolean canUsePermissionCommand(long id, SlashCommandInteractionEvent event) {
        Long lastUsage = cache.getIfPresent(id);

        if (lastUsage != null) {
            long remainingCooldown = TimeUnit.SECONDS.toMillis(15) - (System.currentTimeMillis() - lastUsage);
            String cooldownAwnser = String.format("Please, wait %d seconds to use the permission command again!",
                    remainingCooldown / 1000);

            event.reply(cooldownAwnser).setEphemeral(true).queue();
            return false;
        }

        return true;
    }
    public static void addToCache(long id) {
        cache.put(id, System.currentTimeMillis());
    }

}
