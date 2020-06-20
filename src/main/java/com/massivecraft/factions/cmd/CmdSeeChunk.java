package com.massivecraft.factions.cmd;


import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SeeChunkUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSeeChunk extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private final boolean useParticles;

    public CmdSeeChunk() {
        super();
        aliases.addAll(Aliases.seeChunk);

        useParticles = FactionsPlugin.getInstance().getConfig().getBoolean("see-chunk.particles", true);

        this.requirements = new CommandRequirements.Builder(Permission.SEECHUNK)
                .playerOnly()
                .build();

    }

    @Override
    public void perform(CommandContext context) {
        if (useParticles) {
            boolean toggle = false;
            if (context.args.size() == 0) {
                toggle = !context.fPlayer.isSeeingChunk();
            } else if (context.args.size() == 1) {
                toggle = context.argAsBool(0);
            }
            context.fPlayer.setSeeingChunk(toggle);
            context.msg(TL.COMMAND_SEECHUNK_TOGGLE, toggle ? "enabled" : "disabled");
        } else {
            SeeChunkUtil.showPillars(context.player, context.fPlayer, null, false);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SEECHUNK_DESCRIPTION;
    }
}
