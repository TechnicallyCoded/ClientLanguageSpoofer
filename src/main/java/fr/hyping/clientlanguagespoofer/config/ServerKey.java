package fr.hyping.clientlanguagespoofer.config;

import java.net.IDN;
import java.util.Locale;

/** Canonical keys never perform DNS lookups or combine distinct ports. */
public final class ServerKey {
    private ServerKey() { }

    public static String of(String address) {
        if (address == null || address.isBlank()) return null;
        String value = address.strip();
        String host;
        int port = 25565;
        try {
            if (value.startsWith("[")) {
                int close = value.indexOf(']');
                if (close < 0) return null;
                host = value.substring(1, close);
                String suffix = value.substring(close + 1);
                if (!suffix.isEmpty()) {
                    if (!suffix.startsWith(":")) return null;
                    port = Integer.parseInt(suffix.substring(1));
                }
            } else {
                int first = value.indexOf(':');
                int last = value.lastIndexOf(':');
                if (first >= 0 && first == last) {
                    host = value.substring(0, first);
                    port = Integer.parseInt(value.substring(first + 1));
                } else host = value;
            }
            if (port < 1 || port > 65535 || host.isBlank() || host.chars().anyMatch(Character::isWhitespace)) return null;
            if (!host.contains(":")) host = IDN.toASCII(host);
            if (host.endsWith(".")) host = host.substring(0, host.length() - 1);
            if (host.isBlank() || host.contains("/") || host.contains("@")) return null;
            host = host.toLowerCase(Locale.ROOT);
            return (host.contains(":") ? "[" + host + "]" : host) + ":" + port;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
