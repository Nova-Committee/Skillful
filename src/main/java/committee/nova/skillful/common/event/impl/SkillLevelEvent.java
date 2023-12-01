package committee.nova.skillful.common.event.impl;


import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.world.entity.player.Player;

public class SkillLevelEvent extends SkillEvent {
    private final long oldLevel;
    private final long newLevel;

    public SkillLevelEvent(Player player, SkillInstance skill, long oldLevel, long newLevel) {
        super(player, skill);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public long getOldLevel() {
        return oldLevel;
    }

    public long getNewLevel() {
        return newLevel;
    }

    public boolean isUp() {
        return newLevel > oldLevel;
    }

    public boolean isDown() {
        return newLevel < oldLevel;
    }
}
