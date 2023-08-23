package br.com.techsneeker.envs;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.Map;

public class Environment {

    private static final String PATH = "/envs/envs.yml";
    private static Yaml yml = new Yaml();
    private Map<String, Object> ymlValue;

    public Environment() {
        try (InputStream inputStream = Environment.class.getResourceAsStream(PATH)) {
            if (inputStream != null) {
                this.ymlValue = yml.load(inputStream);
            } else {
                throw new RuntimeException("I didn't find your env file :(");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the env file", e);
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
