package com.massivecraft.factions.zcore.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiscUtil {

    // Map to store locks for different files based on filename.
    private static final Map<String, Lock> LOCKS = new ConcurrentHashMap<>();

    public static byte[] readBytes(Path path) throws IOException {
        // Efficient reading using a buffered stream.
        try (InputStream inputStream = Files.newInputStream(path);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[8192]; // 8 KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    public static void writeBytes(Path path, byte[] bytes) throws IOException {
        // Buffered output to handle large writes efficiently.
        try (OutputStream outputStream = Files.newOutputStream(path,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(outputStream)) {

            bufferedOut.write(bytes);
        }
    }

    public static void write(Path path, String content) throws IOException {
        writeBytes(path, content.getBytes(StandardCharsets.UTF_8));
    }

    public static String read(Path path) throws IOException {
        return new String(readBytes(path), StandardCharsets.UTF_8);
    }

    public static boolean writeCatch(Path path, final String content, boolean sync) {
        // Use the filename as the key for the lock.
        String name = path.getFileName().toString();
        Lock lock = LOCKS.computeIfAbsent(name, n -> new ReentrantLock());

        if (sync) {
            synchronizedWrite(path, content, lock);
        } else {
            try {
                // Just execute immediately (no async).
                synchronizedWrite(path, content, lock);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private static void synchronizedWrite(Path path, String content, Lock lock) {
        lock.lock();
        try {
            write(path, content);
        } catch (IOException e) {
            System.err.println("Failed to write to " + path + ": " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static String readCatch(Path path) {
        try {
            return read(path);
        } catch (IOException e) {
            System.err.println("Failed to read from " + path + ": " + e.getMessage());
            return null;
        }
    }
}