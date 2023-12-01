package committee.nova.skillful.common.event.impl;

import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class SkillEvent extends Event {
    private final Player player;
    private final SkillInstance skill;

    protected SkillEvent(Player player, SkillInstance skill) {
        this.player = player;
        this.skill = skill;
    }

    public Player getPlayer() {
        return player;
    }

    public SkillInstance getSkill() {
        return skill;
    }
}
