package com.massivecraft.factions.cmd.role;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public final class RoleTextInput {

    private static final ConcurrentMap<UUID, PendingInput> PENDING_INPUTS = new ConcurrentHashMap<>();

    private RoleTextInput() {
    }

    public static void request(Player player, List<String> promptLines, Runnable onCancel, Consumer<String> onSubmit) {
        if (player == null || onSubmit == null) {
            return;
        }

        PENDING_INPUTS.put(player.getUniqueId(), new PendingInput(onCancel, onSubmit));
        player.closeInventory();
        sendLines(player, TL.COMMAND_ROLE_GUI_INPUT_MODE.toString());
        if (promptLines != null) {
            for (String line : promptLines) {
                player.sendMessage(TextUtil.parse(line));
            }
        }
    }

    public static boolean handleChat(AsyncPlayerChatEvent event) {
        if (event == null || event.getPlayer() == null) {
            return false;
        }

        Player player = event.getPlayer();
        PendingInput pending = PENDING_INPUTS.remove(player.getUniqueId());
        if (pending == null) {
            return false;
        }

        event.setCancelled(true);
        String message = event.getMessage() == null ? "" : event.getMessage().trim();
        FactionsPlugin.getScheduler().runGlobal(() -> {
            if (!player.isOnline()) {
                return;
            }

            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage(TL.COMMAND_ROLE_GUI_INPUT_CANCELLED.toString());
                if (pending.onCancel != null) {
                    pending.onCancel.run();
                }
                return;
            }

            pending.onSubmit.accept(message);
        });
        return true;
    }

    public static void clear(UUID playerId) {
        if (playerId != null) {
            PENDING_INPUTS.remove(playerId);
        }
    }

    private static void sendLines(Player player, String value) {
        if (player == null || value == null || value.isEmpty()) {
            return;
        }

        for (String line : value.split("\\r?\\n")) {
            if (!line.isEmpty()) {
                player.sendMessage(TextUtil.parse(line));
            }
        }
    }

    private static final class PendingInput {
        private final Runnable onCancel;
        private final Consumer<String> onSubmit;

        private PendingInput(Runnable onCancel, Consumer<String> onSubmit) {
            this.onCancel = onCancel;
            this.onSubmit = onSubmit;
        }
    }
}
