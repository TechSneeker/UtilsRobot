package br.com.techsneeker.listeners;

import br.com.techsneeker.requests.YandexApi;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.List;

public class TranslationCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("translate")) {
            event.deferReply().queue();

            String lang = event.getOption("lang").getAsString();
            String text = event.getOption("text").getAsString();
            String response = YandexApi.doTranslation(lang, text);

            event.getHook().editOriginal(response).queue();
        }
    }

    public List<OptionData> getOptions() {
        return Arrays.asList(
                new OptionData(OptionType.STRING, "lang", "Lang")
                        .addChoices(this.getLanguages())
                        .setRequired(true),

                new OptionData(OptionType.STRING, "text", "Say me something")
                        .setRequired(true)
        );
    }

    private List<Command.Choice> getLanguages() {
        return Arrays.asList(
                new Command.Choice("PT-EN", "pt-en")
        );
    }

}
