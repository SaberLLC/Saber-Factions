package org.saberdev.corex.addons;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.util.Lazy;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saberdev.corex.CoreAddon;

@CoreAddon(configVariable = "Insta-Sponge-Break")
public class InstaSpongeBreak implements Listener {

    private final Lazy<Material> sponge = Lazy.of(XMaterial.SPONGE::parseMaterial);

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == this.sponge.get()) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

            Faction location = Board.getInstance().getFactionAt(FLocation.wrap(block.getLocation()));
            Faction faction = fPlayer.getFaction();
            if (location.isWilderness() || faction.getId().equals(location.getId())) {
                block.breakNaturally();
                event.setCancelled(true);
            }
        }
    }
}