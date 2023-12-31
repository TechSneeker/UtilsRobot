package br.com.techsneeker.listeners;

import br.com.techsneeker.Main;
import br.com.techsneeker.objects.CooldownManager;
import br.com.techsneeker.objects.Description;
import br.com.techsneeker.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConfigCommand extends ListenerAdapter {

    private static final Logger logger = Logger.getLogger(ConfigCommand.class.getName());

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("survey-permissions")) {

            if (!Utils.isOwner(event)) {
                event.reply("Only the Owner can set permissions!").setEphemeral(true).queue();
                return;
            }

            final User user = event.getUser();
            final long userId = user.getIdLong();

            if (!CooldownManager.canUsePermissionCommand(userId, event)) {
                return;
            }

            final long guildId = event.getGuild().getIdLong();
            final String optionSelected = event.getOption("permissions").getAsString();

            CooldownManager.addToPermCache(userId);

            if (!optionSelected.equals("role")) {
                Main.getDatabaseInstance().addPermConfiguration(guildId, optionSelected);
            }

            if (optionSelected.equals("role")) {
                String roleInserted = event.getOption("role").getAsString();
                Role role = this.extractMentionedRole(roleInserted, event);

                if (role == null) {
                    event.reply("You can only specify a role!").setEphemeral(true).queue();
                    return;
                }

                Main.getDatabaseInstance().addPermConfiguration(guildId, role.getName());
            }

            logger.info(String.format("%s atualizou as permissões pro survey na {guilda:%d}", user.getName(), guildId));
            event.reply("You have successfully set the permission!").setEphemeral(true).queue();
        }
    }

    private Role extractMentionedRole(String value, SlashCommandInteractionEvent event) {
        Pattern pattern = Pattern.compile("[&0-9]+");
        Matcher matcher = pattern.matcher(value);

        Guild guild = Objects.requireNonNull(event.getGuild());
        String mentionedRole = null;

        if (matcher.find())
            mentionedRole = matcher.group();

        return (mentionedRole != null)
                ? guild.getRoleById(mentionedRole.replace("&", ""))
                : null;
    }

    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "permissions", Description.PERMISSIONS)
                        .addChoices(this.getPermissionOptions())
                        .setRequired(true),
                new OptionData(OptionType.STRING, "role", Description.PERMISSIONS_ROLE)
        );
    }

    private List<Command.Choice> getPermissionOptions() {
        return List.of(
                new Command.Choice("All Users", "all"),
                new Command.Choice("Only Owner", "owner"),
                new Command.Choice("By role", "role")
        );
    }

}
