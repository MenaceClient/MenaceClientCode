package dev.menace.utils.session;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class LoginManager {

    public static Alt crackedLogin(String username) {
        Minecraft.getMinecraft().session = new Session(username, UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)).toString(),
                "-", "legacy");

        return new Alt(username);
    }

    public static Alt microsoftEmailLogin(String email, String password) throws MicrosoftAuthenticationException, NoSuchFieldException, IllegalAccessException {
        MicrosoftAuthenticator auth = new MicrosoftAuthenticator();
        MicrosoftAuthResult result = auth.loginWithCredentials(email, password);
        MinecraftProfile profile = result.getProfile();

        Minecraft.getMinecraft().session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "mojang");

        return new Alt(email, password, profile.getName(), result.getRefreshToken());
    }

    public static String microsoftRefreshTokenLogin(String token) throws MicrosoftAuthenticationException {
        MicrosoftAuthenticator auth = new MicrosoftAuthenticator();
        MicrosoftAuthResult result = auth.loginWithRefreshToken(token);
        MinecraftProfile profile = result.getProfile();

        Minecraft.getMinecraft().session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "mojang");

        return result.getRefreshToken();
    }

    public static Alt microsoftBrowserLogin() throws MicrosoftAuthenticationException {
        MicrosoftAuthenticator auth = new MicrosoftAuthenticator();
        MicrosoftAuthResult result = auth.loginWithWebview();
        MinecraftProfile profile = result.getProfile();

        Minecraft.getMinecraft().session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "mojang");

        return new Alt(profile.getName(), result.getAccessToken(), profile.getName(), result.getRefreshToken());
    }

}