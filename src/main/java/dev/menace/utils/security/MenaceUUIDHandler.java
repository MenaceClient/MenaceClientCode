package dev.menace.utils.security;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenaceUUIDHandler {

    private static UUID menaceUUID;

    public static void parseUUID(String str) {
        menaceUUID = UUID.fromString(str);
    }

    public static UUID getUUID() {
        return menaceUUID;
    }

    public static void validate() {
        Matcher matcher = Pattern.compile("([a-f0-9]{8})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{12})").matcher(menaceUUID.toString());
        if (!matcher.matches()) {
            try {
                AntiSkidUtils.terminate("You are using an invalid UUID, please re-download the client, if this keeps happening please contact a developer.", 0x05);
            } catch (Exception e) {
                throw new RuntimeException("You are using an invalid UUID, please re-download the client, if this keeps happening please contact a developer.");
            }
        }
    }
}
