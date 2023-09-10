package dev.menace.utils.session;

public enum LoginMode {

    MICROSOFT("Microsoft"),
    RNG("Random"),
    MICROSOFT_BROWSER("Browser");

    final String name;
    LoginMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
