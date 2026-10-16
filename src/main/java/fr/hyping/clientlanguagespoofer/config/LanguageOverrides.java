package fr.hyping.clientlanguagespoofer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Null means Default. Configuration contains only explicit per-server overrides. */
public final class LanguageOverrides {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;
    private Map<String, String> overrides = Map.of();

    public LanguageOverrides(Path file) {
        this.file = file;
    }

    public synchronized void load() throws IOException {
        if (!Files.exists(file)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            JsonObject servers = root.getAsJsonObject("servers");
            Map<String, String> loaded = new HashMap<>();
            if (servers != null) {
                for (Map.Entry<String, JsonElement> entry : servers.entrySet()) {
                    String key = ServerKey.of(entry.getKey());
                    String code = entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()
                        ? validLanguage(entry.getValue().getAsString()) : null;
                    if (key != null && code != null) loaded.put(key, code);
                }
            }
            overrides = Map.copyOf(loaded);
        } catch (RuntimeException exception) {
            throw new IOException("Invalid language override configuration; existing file was left untouched", exception);
        }
    }

    public synchronized String get(String address) {
        String key = ServerKey.of(address);
        return key == null ? null : overrides.get(key);
    }

    public synchronized void set(String address, String language) throws IOException {
        String key = ServerKey.of(address);
        if (key == null) throw new IllegalArgumentException("Invalid server address");
        String code = language == null ? null : validLanguage(language);
        if (language != null && code == null) throw new IllegalArgumentException("Invalid language code");
        Map<String, String> updated = new HashMap<>(overrides);
        if (code == null) updated.remove(key);
        else updated.put(key, code);
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        root.add("servers", GSON.toJsonTree(updated));
        Path parent = file.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "client-language-spoofer-", ".tmp");
        try {
            Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
            overrides = Map.copyOf(updated);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static String validLanguage(String language) {
        String normalized = language.toLowerCase(Locale.ROOT).replace('-', '_');
        return normalized.length() >= 2 && normalized.length() <= 16 && normalized.matches("[a-z][a-z0-9_]*")
            ? normalized : null;
    }
}
