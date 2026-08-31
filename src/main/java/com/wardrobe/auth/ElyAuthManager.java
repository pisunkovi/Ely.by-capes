package com.wardrobe.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wardrobe.client.texture.AnimatedGifTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class ElyAuthManager {
    private static File configFile = null;
    private static String username = "";
    private static String accessToken = "";
    private static boolean authenticated = false;
    
    private static Identifier skinTextureId = null;
    private static Identifier customCapeId = null;
    private static Identifier customElytraId = null;
    private static AnimatedGifTexture animatedCape = null;
    private static boolean simultaneousRender = true;
    private static boolean skinLoading = false;

    public static void init() {
        loadConfig();
    }

    public static boolean isAuthenticated() {
        return authenticated && !username.isEmpty();
    }

    public static String getUsername() {
        return username;
    }

    public static Identifier getSkinTextureId() {
        if (skinTextureId == null && isAuthenticated() && !skinLoading) {
            fetchUserSkin();
        }
        return skinTextureId;
    }

    public static Identifier getActiveCapeTexture() {
        if (animatedCape != null && !animatedCape.isEmpty()) {
            return animatedCape.getCurrentTexture();
        }
        return customCapeId;
    }

    public static void setCustomCapeId(Identifier id) {
        customCapeId = id;
        animatedCape = null;
    }

    public static void setAnimatedCape(AnimatedGifTexture gif) {
        animatedCape = gif;
        customCapeId = null;
    }

    public static Identifier getCustomElytraId() {
        return customElytraId;
    }

    public static void setCustomElytraId(Identifier id) {
        customElytraId = id;
    }

    public static boolean isSimultaneousRender() {
        return simultaneousRender;
    }

    public static void setSimultaneousRender(boolean value) {
        simultaneousRender = value;
    }

    public static void login(String user, String token) {
        username = user.trim();
        accessToken = token.trim();
        authenticated = !username.isEmpty();
        skinTextureId = null;
        saveConfig();
        if (authenticated) {
            fetchUserSkin();
        }
    }

    public static void logout() {
        username = "";
        accessToken = "";
        authenticated = false;
        skinTextureId = null;
        customCapeId = null;
        customElytraId = null;
        animatedCape = null;
        saveConfig();
    }

    public static void fetchUserSkin() {
        if (username.isEmpty() || skinLoading) return;
        skinLoading = true;
        
        Thread thread = new Thread(() -> {
            try {
                URI uri = URI.create("http://skinsystem.ely.by/skins/" + username + ".png");
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);
                conn.connect();

                if (conn.getResponseCode() == 200) {
                    try (InputStream stream = conn.getInputStream()) {
                        NativeImage image = NativeImage.read(stream);
                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client != null) {
                            client.execute(() -> {
                                skinTextureId = client.getTextureManager().registerDynamicTexture(
                                        "ely_skin_" + username.toLowerCase(),
                                        new NativeImageBackedTexture(image)
                                );
                                skinLoading = false;
                            });
                        }
                    }
                } else {
                    skinLoading = false;
                }
            } catch (Exception e) {
                skinLoading = false;
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private static File getConfigFile() {
        if (configFile == null) {
            MinecraftClient client = MinecraftClient.getInstance();
            File runDir = (client != null && client.runDirectory != null) ? client.runDirectory : new File(".");
            configFile = new File(runDir, "config/ely_auth.json");
        }
        return configFile;
    }

    private static void loadConfig() {
        try {
            File file = getConfigFile();
            if (!file.exists()) return;
            try (FileReader reader = new FileReader(file)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                username = json.has("username") ? json.get("username").getAsString() : "";
                accessToken = json.has("token") ? json.get("token").getAsString() : "";
                authenticated = json.has("authenticated") && json.get("authenticated").getAsBoolean();
                simultaneousRender = !json.has("simultaneousRender") || json.get("simultaneousRender").getAsBoolean();
            }
        } catch (Exception ignored) {}
    }

    private static void saveConfig() {
        try {
            File file = getConfigFile();
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            JsonObject json = new JsonObject();
            json.addProperty("username", username);
            json.addProperty("token", accessToken);
            json.addProperty("authenticated", authenticated);
            json.addProperty("simultaneousRender", simultaneousRender);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString());
            }
        } catch (Exception ignored) {}
    }
}
