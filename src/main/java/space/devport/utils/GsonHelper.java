package space.devport.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GsonHelper {

    private final DevportPlugin plugin;

    private final Gson gson;

    public GsonHelper(DevportPlugin plugin, boolean prettyPrinting) {
        this.plugin = plugin;
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (prettyPrinting)
            gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
    }

    public GsonHelper(DevportPlugin plugin) {
        this(plugin, false);
    }

    public <T> T load(String dataPath, Type type) {

        Path path = Paths.get(dataPath);

        if (!Files.exists(path)) return null;

        String input;
        try {
            input = String.join("", Files.readAllLines(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (Strings.isNullOrEmpty(input)) return null;

        plugin.getConsoleOutput().debug("JSON: " + input);

        return gson.fromJson(input, type);
    }

    public <T> void save(final T in, String dataPath) {

        String output = gson.toJson(in, new TypeToken<T>() {
        }.getType());

        plugin.getConsoleOutput().debug("JSON: " + output);

        Path path = Paths.get(dataPath);

        try {
            Files.write(path, output.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}