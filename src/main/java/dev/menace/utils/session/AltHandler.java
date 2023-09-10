package dev.menace.utils.session;

import com.google.gson.JsonObject;
import dev.menace.utils.file.FileManager;
import dev.menace.utils.security.EncryptionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AltHandler {

    private static final List<Alt> altList = new ArrayList<>();

    public static void addAlt(Alt alt) {
        altList.add(alt);
        saveAlts();
    }

    public static void removeAlt(Alt alt) {
        altList.remove(alt);
        saveAlts();
    }

    public static void saveAlts() {
        JsonObject altObject = new JsonObject();
        altList.forEach(alt -> {
            JsonObject altSave = new JsonObject();
            altSave.addProperty("Email", alt.getEmail());
            altSave.addProperty("Password", alt.getPassword());
            altSave.addProperty("RefreshToken", alt.getRefreshToken());
            altObject.add(alt.getUsername(), altSave);
        });
        String altJson = altObject.toString();

        try {
            byte[] encryptedAlts = EncryptionUtils.encrypt(altJson, EncryptionUtils.getKey());
            File altFile = new File(FileManager.getMenaceFolder(), "alts.txt");
            altFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(altFile);
            outputStream.write(encryptedAlts);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadAlts() {
        File altFile = new File(FileManager.getMenaceFolder(), "alts.txt");
        if (!altFile.exists()) {
            return;
        }
        try {
            FileInputStream inputStream = new FileInputStream(altFile);
            byte[] encryptedAlts = new byte[inputStream.available()];
            inputStream.read(encryptedAlts);
            inputStream.close();
            String decryptedAlts = EncryptionUtils.decrypt(encryptedAlts, EncryptionUtils.getKey());
            JsonObject altObject = FileManager.getGson().fromJson(decryptedAlts, JsonObject.class);
            altObject.entrySet().forEach(entry -> {
                JsonObject altSave = entry.getValue().getAsJsonObject();
                String email = altSave.get("Email").getAsString();
                String password = altSave.get("Password").getAsString();
                String refreshToken = altSave.get("RefreshToken").getAsString();
                Alt alt = new Alt(email, password, entry.getKey(), refreshToken);
                altList.add(alt);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Alt> getAlts() {
        return altList;
    }

}
