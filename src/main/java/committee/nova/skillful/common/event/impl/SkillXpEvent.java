package committee.nova.skillful.common.event.impl;

import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.world.entity.player.Player;

public class SkillXpEvent extends SkillEvent {
    private final long change;

    public SkillXpEvent(Player player, SkillInstance skill, long change) {
        super(player, skill);
        this.change = change;
    }

    public long getChange() {
        return change;
    }

    public boolean isUp() {
        return change > 0;
    }

    public boolean isDown() {
        return change < 0;
    }
}
