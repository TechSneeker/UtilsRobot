package br.com.techsneeker.envs;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class Environment {

    private static final String PATH = "src/main/java/br/com/techsneeker/envs/envs.yml";

    private static Yaml yml = new Yaml();
    private Map<String, Object> ymlValue;

    public Environment() {
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(PATH);
            this.ymlValue = yml.load(inputStream);
        } catch (FileNotFoundException e) {

            throw new RuntimeException("I didn't find your env file :(");

        } finally {

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public String getDiscordToken() {
        var token = (Map<String, Object>) this.ymlValue.getOrDefault("tokens", Collections.emptyMap());
        return (String) token.getOrDefault("discord-token", null);
    }

    public String getYandexToken() {
        var token = (Map<String, Object>) this.ymlValue.getOrDefault("tokens", Collections.emptyMap());
        return (String) token.getOrDefault("yandex-token", null);
    }
}
