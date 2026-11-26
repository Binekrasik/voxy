package me.cortex.voxy.commonImpl;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;

import java.nio.file.Path;

/**
 * Mod loader queries that are safe to use from mixin plugins and static initialisers,
 * before the NeoForge {@code ModList} exists.
 */
public final class Platform {
    private Platform() {}

    public static boolean isModLoaded(String modId) {
        var list = LoadingModList.get();
        return list != null && list.getModFileById(modId) != null;
    }

    public static boolean isDedicatedServer() {
        return FMLLoader.getDist().isDedicatedServer();
    }

    public static Path getGameDir() {
        return FMLLoader.getGamePath();
    }

    public static Path getConfigDir() {
        return FMLLoader.getGamePath().resolve("config");
    }

    /** Root of the voxy mod jar (or the classes output directory in a dev environment). */
    public static Path getModRoot() {
        return LoadingModList.get().getModFileById("voxy").getFile().getSecureJar().getRootPath();
    }

    public static String getModVersion() {
        var info = LoadingModList.get().getModFileById("voxy");
        return info == null ? null : info.versionString();
    }
}
