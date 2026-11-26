package me.cortex.voxy.commonImpl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/** Typed {@link CompoundTag} reads with a fallback for missing or mistyped keys. */
public final class NbtUtil {
    private NbtUtil() {}

    public static int getIntOr(CompoundTag tag, String key, int fallback) {
        return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getInt(key) : fallback;
    }

    public static long getLongOr(CompoundTag tag, String key, long fallback) {
        return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getLong(key) : fallback;
    }

    public static String getStringOr(CompoundTag tag, String key, String fallback) {
        return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : fallback;
    }
}
