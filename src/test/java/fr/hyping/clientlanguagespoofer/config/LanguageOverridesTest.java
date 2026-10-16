package fr.hyping.clientlanguagespoofer.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LanguageOverridesTest {
    @TempDir Path directory;

    @Test
    void persistsOverridesPerServerAndResetDoesNotAffectOtherServers() throws Exception {
        Path file = directory.resolve("overrides.json");
        LanguageOverrides store = new LanguageOverrides(file);
        assertNull(store.get("example.org"));
        store.set("Example.ORG", "fr_fr");
        store.set("other.example:25566", "en_us");
        store.set("[::1]:25566", "de_de");
        LanguageOverrides reopened = new LanguageOverrides(file);
        reopened.load();
        assertEquals("fr_fr", reopened.get("example.org:25565"));
        assertEquals("en_us", reopened.get("other.example:25566"));
        assertEquals("de_de", reopened.get("[::1]:25566"));
        reopened.set("example.org", null);
        LanguageOverrides reset = new LanguageOverrides(file);
        reset.load();
        assertNull(reset.get("example.org"));
        assertEquals("en_us", reset.get("other.example:25566"));
        assertNull(reset.get("other.example"));
    }

    @Test
    void keepsExplicitOverrideEvenIfItMatchesGlobalLanguageAndRejectsInvalidCodes() throws Exception {
        LanguageOverrides store = new LanguageOverrides(directory.resolve("overrides.json"));
        store.set("example.org", "en_us");
        assertEquals("en_us", store.get("example.org"));
        assertThrows(IllegalArgumentException.class, () -> store.set("example.org", "a".repeat(17)));
        assertThrows(IllegalArgumentException.class, () -> store.set("example.org", "en us"));
        assertEquals("en_us", store.get("example.org"));
    }

    @Test
    void failedSaveDoesNotChangeLiveOverridesOrLeaveTemporaryFiles() throws Exception {
        Path file = directory.resolve("not-a-file");
        Files.createDirectory(file);
        LanguageOverrides store = new LanguageOverrides(file);
        assertThrows(IOException.class, () -> store.set("example.org", "fr_fr"));
        assertNull(store.get("example.org"));
        try (Stream<Path> children = Files.list(directory)) {
            assertEquals(1, children.count());
        }
    }

    @Test
    void malformedConfigurationIsNotOverwrittenOnLoad() throws Exception {
        Path file = directory.resolve("overrides.json");
        Files.writeString(file, "broken JSON");
        LanguageOverrides store = new LanguageOverrides(file);
        assertThrows(IOException.class, store::load);
        assertEquals("broken JSON", Files.readString(file));
    }

    @Test
    void canonicalizesHostCaseDefaultPortsIpv6AndUnicodeWithoutDns() {
        assertEquals("example.org:25565", ServerKey.of(" Example.ORG. "));
        assertEquals(ServerKey.of("example.org"), ServerKey.of("example.org:25565"));
        assertNotEquals(ServerKey.of("example.org"), ServerKey.of("example.org:25566"));
        assertEquals("[::1]:25565", ServerKey.of("::1"));
        assertEquals(ServerKey.of("[::1]:25565"), ServerKey.of("[::1]"));
        assertEquals("xn--bcher-kva.example:25565", ServerKey.of("bücher.example"));
        assertNull(ServerKey.of("example.org:0"));
        assertNull(ServerKey.of("example.org:not-a-port"));
        assertNull(ServerKey.of(""));
    }
}
