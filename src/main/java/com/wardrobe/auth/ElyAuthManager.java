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
    private static final File CONFIG_FILE = new File(MinecraftClient.getInstance().runDirectory, "config/ely_auth.json");
    private static String username = "";
    private static String accessToken = "";
    private static boolean authenticated = false;
    
    private static Identifier skinTextureId = null;
    private static Identifier customCapeId = null;
    private static Identifier customElytraId = null;
    private static AnimatedGifTexture animatedCape = null;
    private static boolean simultaneousRender = true;

    public static void init() {
        loadConfig();
        if (isAuthenticated()) {
            fetchUserSkin();
        }
    }

    public static boolean isAuthenticated() {
        return authenticated && !username.isEmpty();
    }

    public static String getUsername() {
        return username;
    }

    public static Identifier getSkinTextureId() {
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
        if (username.isEmpty()) return;
        new Thread(() -> {
            try {
                URI uri = URI.create("http://skinsystem.ely.by/skins/" + username + ".png");
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.connect();

                if (conn.getResponseCode() == 200) {
                    try (InputStream stream = conn.getInputStream()) {
                        NativeImage image = NativeImage.read(stream);
                        MinecraftClient.getInstance().execute(() -> {
                            skinTextureId = MinecraftClient.getInstance().getTextureManager()
                                    .registerDynamicTexture("ely_skin_" + username.toLowerCase(), new NativeImageBackedTexture(image));
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void loadConfig() {
        if (!CONFIG_FILE.exists()) return;
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            username = json.has("username") ? json.get("username").getAsString() : "";
            accessToken = json.has("token") ? json.get("token").getAsString() : "";
            authenticated = json.has("authenticated") && json.get("authenticated").getAsBoolean();
            simultaneousRender = !json.has("simultaneousRender") || json.get("simultaneousRender").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveConfig() {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) CONFIG_FILE.getParentFile().mkdirs();
            JsonObject json = new JsonObject();
            json.addProperty("username", username);
            json.addProperty("token", accessToken);
            json.addProperty("authenticated", authenticated);
            json.addProperty("simultaneousRender", simultaneousRender);
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                writer.write(json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
