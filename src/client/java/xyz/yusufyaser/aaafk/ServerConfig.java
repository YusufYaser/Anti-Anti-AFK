package xyz.yusufyaser.aaafk;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    public static int CONFIG_VERSION = 1;

    private static Map<String, ServerConfig> instances = new HashMap<>();

    private static final Gson gson = new Gson();
    private File configFile = null;
    private final String server;

    public boolean enabled = false;
    public int afkTimer = 0;
    public ArrayList<String> messages = new ArrayList<>();
    public boolean hideMessages = false;

    private ServerConfig(String server) {
        this.server = server;
        if (server == null) return;
        configFile = new File(MinecraftClient.getInstance().runDirectory, "config/anti-anti-afk/" + server + ".json");
        if (!configFile.exists()) return;
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            enabled = json.get("enabled").getAsBoolean();
            afkTimer = json.get("afkTimer").getAsInt();
            hideMessages = json.get("hideMessages").getAsBoolean();
            for (JsonElement jsonElement : json.get("messages").getAsJsonArray()) {
                messages.add(jsonElement.getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized ServerConfig getInstance(String server) {
        if (!instances.containsKey(server)) {
            instances.put(server, new ServerConfig(server));
        }
        return instances.get(server);
    }

    public boolean save() {
        Path directoryPath = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "config/anti-anti-afk");
        try {
            if (Files.notExists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            return false;
        }
        try (FileWriter writer = new FileWriter(configFile)) {
            JsonObject config = new JsonObject();
            config.addProperty("configVersion", CONFIG_VERSION);
            config.addProperty("enabled", enabled);
            config.addProperty("afkTimer", afkTimer);
            config.addProperty("hideMessages", hideMessages);
            config.add("messages", gson.toJsonTree(messages));
            gson.toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getServer() {
        return server;
    }
}
