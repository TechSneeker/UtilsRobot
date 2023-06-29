package br.com.techsneeker.requests;

import br.com.techsneeker.envs.Environment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class YandexApi {

    private static final Environment variables = new Environment();
    private static final String API = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private static final HttpClient CLIENT = HttpClient.newBuilder().build();

    public static String doTranslation(String language, String content) {

        StringBuilder builder = new StringBuilder(API);
        builder.append("?key=").append(variables.getYandexToken());
        builder.append("&lang=").append(language);
        builder.append("&text=").append(URLEncoder.encode(content, StandardCharsets.UTF_8));
        builder.append("&format=").append("plain");

        String url = builder.toString();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        HttpResponse<String> response = null;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException("I couldn't communicate with Yandex :(");
        } catch (InterruptedException e) {
            throw new RuntimeException("My communication was interrupted :(");
        }

        JsonObject body = new Gson().fromJson(response.body(), JsonObject.class);

        String translation = body.get("text").getAsString();
        short statusCode = body.get("code").getAsShort();

        switch (statusCode) {
            case 402:
                throw new RuntimeException("The API key is blocked");
            case 413:
                throw new RuntimeException("The maximum allowed text size is exceeded");
            case 422:
                throw new RuntimeException("The text can't be translated");
            case 501:
                throw new RuntimeException("The specified translation direction isn't supported");
        }

        return translation;
    }

}
