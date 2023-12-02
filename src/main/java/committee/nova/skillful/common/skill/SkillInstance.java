package committee.nova.skillful.common.skill;

import committee.nova.skillful.common.event.impl.SkillLevelEvent;
import committee.nova.skillful.common.util.Utilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SkillInstance implements ISkillRecord {
    private final ISkillType skill;
    private long level;
    private long xp;

    private SkillInstance(ISkillType skill) {
        this.skill = skill;
    }

    public static SkillInstance of(ISkillType skill) {
        return new SkillInstance(skill);
    }

    public ISkillType getSkill() {
        return skill;
    }

    public boolean acquired() {
        return level > 0;
    }

    public boolean clueless() {
        return !acquired() && xp == 0;
    }

    public boolean mastered() {
        return level >= skill.getMaxLevel();
    }

    public void changeXp(ServerPlayer player, long change) {
        if (Utilities.isPlayerFake(player)) return;
        if (change >= 0) _addXp(player, change);
        else _reduceXp(player, -change);
    }

    public void changeToLevel(ServerPlayer player, long target) {
        if (Utilities.isPlayerFake(player)) return;
        final long level0 = level;
        level = Math.max(0, Math.min(target, skill.getMaxLevel()));
        if (level0 == level) return;
        xp = 0;
        MinecraftForge.EVENT_BUS.post(new SkillLevelEvent(player, this, level0, level));
        skill.onLevelChange(player, this, level0, level);
        syncToClient(player);
    }

    private void _addXp(ServerPlayer player, long change) {
        if (change == 0) return;
        if (change < 0) {
            _reduceXp(player, -change);
            return;
        }
        if (mastered()) return;
        xp += Math.min(change, Long.MAX_VALUE - xp);
        final long level0 = level;
        while (true) {
            final long required = skill.getLevelRequiredXp(player, level);
            if (xp < required) break;
            level++;
            if (level > skill.getMaxLevel()) {
                complete();
                break;
            }
            xp -= required;
        }
        if (level0 != level) {
            skill.onLevelChange(player, this, level0, level);
            MinecraftForge.EVENT_BUS.post(new SkillLevelEvent(player, this, level0, level));
        }
        syncToClient(player);
    }

    private void _reduceXp(ServerPlayer player, long change) {
        if (change == 0) return;
        if (change < 0) {
            _addXp(player, -change);
            return;
        }
        final long level0 = level;
        if (clueless()) return;
        xp -= change;
        while (xp < 0) {
            level--;
            if (level < 0) {
                clear();
                break;
            }
            xp += skill.getLevelRequiredXp(player, level);
        }
        if (level0 != level) {
            skill.onLevelChange(player, this, level0, level);
            MinecraftForge.EVENT_BUS.post(new SkillLevelEvent(player, this, level0, level));
        }
        syncToClient(player);
    }

    private void complete() {
        level = skill.getMaxLevel();
        xp = 0;
    }

    private void clear() {
        level = 0;
        xp = 0;
    }

    @Override
    public ResourceLocation getId() {
        return skill.getId();
    }

    @Override
    public long getLevel() {
        return level;
    }

    @Override
    public long getXp() {
        return xp;
    }

    @Override
    public void setLevel(long level) {
        this.level = level;
    }

    @Override
    public void setXp(long xp) {
        this.xp = xp;
    }
}
