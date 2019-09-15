package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAutoClaim extends FCommand {

     public CmdAutoClaim() {
          super();
          this.aliases.add("autoclaim");

          //this.requiredArgs.add("");
          this.optionalArgs.put("faction", "your");

          this.requirements = new CommandRequirements.Builder(Permission.AUTOCLAIM)
                  .playerOnly()
                  .withAction(PermissableAction.TERRITORY)
                  .build();
     }

     @Override
     public void perform(CommandContext context) {
          Faction forFaction = context.argAsFaction(0, context.faction);

          if (forFaction != context.fPlayer.getFaction()) {
               if (!context.fPlayer.isAdminBypassing()) {
                    if (forFaction.getAccess(context.fPlayer, PermissableAction.TERRITORY) != Access.ALLOW) {
                         context.msg(TL.COMMAND_CLAIM_DENIED);
                         return;
                    }
               }
          }

          if (forFaction == null || forFaction == context.fPlayer.getAutoClaimFor()) {
               context.fPlayer.setAutoClaimFor(null);
               context.msg(TL.COMMAND_AUTOCLAIM_DISABLED);
               return;
          }

          if (!context.fPlayer.canClaimForFaction(forFaction)) {
               if (context.faction == forFaction) {
                    context.msg(TL.COMMAND_AUTOCLAIM_REQUIREDRANK, Role.MODERATOR.getTranslation());
               } else {
                    context.msg(TL.COMMAND_AUTOCLAIM_OTHERFACTION, forFaction.describeTo(context.fPlayer));
               }

               return;
          }

          context.fPlayer.setAutoClaimFor(forFaction);

          context.msg(TL.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(context.fPlayer));
          context.fPlayer.attemptClaim(forFaction, context.fPlayer.getPlayer().getLocation(), true);
     }

     @Override
     public TL getUsageTranslation() {
          return TL.COMMAND_AUTOCLAIM_DESCRIPTION;
     }

}