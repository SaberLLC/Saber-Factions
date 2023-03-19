package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.util.FastMath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author evilmidget38
 */

public class UUIDFetcher {

    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final double PROFILES_PER_REQUEST = 100;
    private static final Executor THREAD_POOL = Executors.newFixedThreadPool(3);

    private static UUIDFetcher instance;

    public static UUIDFetcher getInstance() {
        if (instance == null) {
            instance = new UUIDFetcher();
        }
        return instance;
    }

    public FetchingSession newSession(List<String> usernames) {
        return new FetchingSession(usernames);
    }

    public UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public static final class FetchingSession {

        private final List<String> usernames;

        private static final JSONParser JSON_PARSER = new JSONParser();

        public FetchingSession(List<String> usernames) {
            this.usernames = new ArrayList<>(usernames);
        }

        public CompletionStage<Map<String, UUID>> fetch() {
            int size = this.usernames.size();
            int requests = FastMath.ceil(size / PROFILES_PER_REQUEST);

            return CompletableFuture.supplyAsync(() -> {
                Map<String, UUID> uuids = new HashMap<>();
                for (int i = 0; i < requests; i++) {
                    HttpURLConnection connection = null;
                    try {
                        connection = createConnection();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String body = JSONArray.toJSONString(this.usernames.subList(i * 100, Math.min((i + 1) * 100, size)));
                    try {
                        writeBody(connection, body);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    JSONArray array;
                    try {
                        array = (JSONArray) JSON_PARSER.parse(new InputStreamReader(connection.getInputStream()));
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    for (Object profile : array) {
                        JSONObject jsonProfile = (JSONObject) profile;
                        String id = (String) jsonProfile.get("id");
                        String name = (String) jsonProfile.get("name");
                        UUID uuid = UUIDFetcher.getInstance().getUUID(id);
                        uuids.put(name, uuid);
                    }
                    if (i != requests - 1) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                }
                return uuids;
            }, THREAD_POOL);
        }

        private HttpURLConnection createConnection() throws IOException {
            URL url = new URL(PROFILE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            return connection;
        }

        private void writeBody(HttpURLConnection connection, String body) throws IOException {
            OutputStream stream = connection.getOutputStream();
            stream.write(body.getBytes());
            stream.flush();
            stream.close();
        }
    }
}