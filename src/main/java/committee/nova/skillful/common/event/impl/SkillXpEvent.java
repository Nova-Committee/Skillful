package committee.nova.skillful.common.event.impl;

import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.world.entity.player.Player;

public class SkillXpEvent extends SkillEvent {
    private final long change;
    private final long lvlChange;

    public SkillXpEvent(Player player, SkillInstance skill, long change, long lvlChange) {
        super(player, skill);
        this.change = change;
        this.lvlChange = lvlChange;
    }

    public long getChange() {
        return change;
    }

    public long getLvlChange() {
        return lvlChange;
    }

    public boolean isUp() {
        return lvlChange > 0 || (lvlChange == 0 && change > 0);
    }

    public boolean isDown() {
        return lvlChange < 0 || (lvlChange == 0 && change < 0);
    }
}
