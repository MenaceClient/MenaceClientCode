package dev.menace;

import de.florianmichael.viamcp.ViaMCP;
import dev.menace.command.CommandManager;
import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.bus.Bus;
import dev.menace.event.bus.impl.EventBus;
import dev.menace.event.events.Event;
import dev.menace.event.events.EventKey;
import dev.menace.module.Module;
import dev.menace.module.ModuleManager;
import dev.menace.module.config.ConfigManager;
import dev.menace.ui.hud.HUDManager;
import dev.menace.utils.file.FileManager;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.misc.DiscordRP;
import dev.menace.utils.security.MenaceUser;
import dev.menace.utils.session.LoginManager;
import dev.menace.utils.render.font.Fonts;
import dev.menace.utils.render.font.MenaceFontRenderer;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.pircbotx.exception.IrcException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Menace {

    public static Menace instance = new Menace();
    public boolean starting = false;
    public Minecraft mc = Minecraft.getMinecraft();
    public String apiURL = "https://api.menaceclient.me/";
    public MenaceUser user;
    public DiscordRP discordRP;
    public Bus<Event> eventManager;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public HUDManager hudManager;
    public ConfigManager configManager;

    //Fonts
    public MenaceFontRenderer sfPro;
    public MenaceFontRenderer sfPro30;
    public MenaceFontRenderer productSans20;
    public MenaceFontRenderer productSans24;
    public MenaceFontRenderer ascii24;
    public MenaceFontRenderer ascii18;
    public MenaceFontRenderer jetbrainsMono;

    public void initFonts() {
        sfPro = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/SF-Pro.ttf"), 20, Font.PLAIN), true);
        sfPro30 = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/SF-Pro.ttf"), 30, Font.PLAIN), true);
        productSans20 = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/ProductSans.ttf"), 20, Font.PLAIN), true);
        productSans24 = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/ProductSans.ttf"), 20, Font.PLAIN), true);
        ascii24 = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/ascii.ttf"), 24, Font.PLAIN),  true);
        ascii18 = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/ascii.ttf"), 18, Font.PLAIN),  true);
        jetbrainsMono = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/JetBrainsMono-Regular.ttf"), 15, Font.PLAIN), true);
    }

    public void startClient() {

        starting = true;
        System.out.println("[Menace] Starting Client...");

        Display.setTitle("Menace 1.8.9");

        FileManager.init();

        discordRP = new DiscordRP();
        discordRP.start();

        eventManager = new EventBus<>();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        commandManager.init();
        hudManager = new HUDManager();

        try {
            ViaMCP.create();

            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File yes = new File("D:/ez/lol/sex/halal/____.____");

        if (yes.exists()) {
            try {
                Scanner bs = new Scanner(yes);
                String skul = bs.nextLine();
                LoginManager.microsoftEmailLogin(skul.split(":")[0], skul.split(":")[1]);
            } catch (FileNotFoundException | MicrosoftAuthenticationException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        configManager = new ConfigManager();

        if (!(new File(FileManager.getConfigFolder(), "Binds.json").exists())) {
            moduleManager.saveKeys();
        }

        eventManager.subscribe(this);

        starting = false;
    }

    public void postinit() {

    }

    public void stopClient() {
        System.out.println("[Menace] Stopping Client...");
        discordRP.stop();
        hudManager.saveElements();
        commandManager.end();
    }

    @EventLink
    public Listener<EventKey> onKeypress = event -> {
        moduleManager.getModules().stream().filter(module -> module.getKeybind() == event.getKey()).forEach(Module::toggle);
    };

}
