package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

    private static final HashMap<String, Lock> LOCKS = new HashMap<>();

    public static byte[] readBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            out.write(bytes);
        }
    }

    public static void write(File file, String content) throws IOException {
        writeBytes(file, content.getBytes(StandardCharsets.UTF_8));
    }

    public static String read(File file) throws IOException {
        return new String(readBytes(file), StandardCharsets.UTF_8);
    }

    public static boolean writeCatch(final File file, final String content, boolean sync) {
        String name = file.getName();
        Lock lock = LOCKS.computeIfAbsent(name, n -> new ReentrantReadWriteLock().writeLock());

        Runnable write = () -> {
            lock.lock();
            try {
                write(file, content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };

        if (sync) {
            write.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), write);
        }
        return true;
    }

    public static String readCatch(File file) {
        try {
            return read(file);
        } catch (IOException e) {
            return null;
        }
    }
}