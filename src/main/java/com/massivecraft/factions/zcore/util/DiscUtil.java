package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

    private static final HashMap<String, Lock> LOCKS = new HashMap<>();

    public static byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public static void writeBytes(Path path, byte[] bytes) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE))) {
            out.write(bytes);
        }
    }

    public static void write(Path path, String content) throws IOException {
        writeBytes(path, content.getBytes(StandardCharsets.UTF_8));
    }

    public static String read(Path path) throws IOException {
        return new String(readBytes(path), StandardCharsets.UTF_8);
    }

    public static boolean writeCatch(Path path, final String content, boolean sync) {
        String name = path.getFileName().toString();
        Lock lock = LOCKS.computeIfAbsent(name, n -> new ReentrantReadWriteLock().writeLock());

        Runnable write = () -> {
            lock.lock();
            try {
                write(path, content);
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

    public static String readCatch(Path path) {
        try {
            return read(path);
        } catch (IOException e) {
            return null;
        }
    }
}