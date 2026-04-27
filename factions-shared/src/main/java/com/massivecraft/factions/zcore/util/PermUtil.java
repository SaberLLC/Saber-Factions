package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.zcore.MPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;

public class PermUtil {

    public Map<String, String> permissionDescriptions = new HashMap<>();

    protected MPlugin p;

    public PermUtil(MPlugin p) {
        this.p = p;
        this.setup();
    }

    public String getForbiddenMessage(String perm) {
        return TextUtil.parse(TL.GENERIC_NOPERMISSION.toString(), getPermissionDescription(perm));
    }

    /**
     * This method hooks into all permission plugins we are supporting
     */
    public final void setup() {
        for (Permission permission : p.getDescription().getPermissions()) {
            this.permissionDescriptions.put(permission.getName(), permission.getDescription());
        }
    }

    public String getPermissionDescription(String perm) {
        String desc = permissionDescriptions.get(perm);
        return desc != null ? desc : TL.GENERIC_DOTHAT.toString();
    }

    /**
     * This method tests if me has a certain permission and returns true if me has. Otherwise false
     */
    public boolean has(CommandSender me, String perm) {
        return me != null && me.hasPermission(perm);
    }

    public boolean has(CommandSender me, String perm, boolean informSenderIfNot) {
        if (has(me, perm))
            return true;
        else if (informSenderIfNot && me != null)
            me.sendMessage(this.getForbiddenMessage(perm));
        return false;
    }
}
