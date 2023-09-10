package dev.menace.utils.session;

import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import net.minecraft.util.EnumChatFormatting;

public class Alt {

    private final String email;
    private final String password;
    private final String username;
    private String refreshToken;
    private boolean isCracked = false;

    public Alt(String email, String password) {
        this.email = email;
        this.password = password;
        this.username = email;
        this.isCracked = false;
    }

    public Alt(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isCracked = false;
    }

    public Alt(String email, String password, String username, String refreshToken) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isCracked = false;
        this.refreshToken = refreshToken;
    }

    public Alt(String username) {
        this.username = username;
        this.email = username;
        this.password = "-";
        this.isCracked = true;
    }

    public boolean isCracked() {
        return this.isCracked;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public void login() {
        if (isCracked) {
            LoginManager.crackedLogin(username);
            return;
        }

        new Thread(() -> {
            try {
                refreshToken = LoginManager.microsoftRefreshTokenLogin(refreshToken);
                AltHandler.saveAlts();
            } catch (MicrosoftAuthenticationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
