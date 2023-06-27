package br.com.techsneeker.envs;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;

public class Environment {

    private static final String PATH = "src/main/java/br/com/techsneeker/envs/envs.yml";

    private static Yaml yml = new Yaml();
    private Map<String, Object> ymlValue;

    public Environment() {
        try {
            this.ymlValue = yml.load(new FileInputStream(PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("I didn't find your env file :(");
        }
    }

    public String getDiscordToken() {
        var token = (Map<String, Object>) this.ymlValue.getOrDefault("tokens", Collections.emptyMap());
        return (String) token.getOrDefault("discord-token", "");
    }
}
