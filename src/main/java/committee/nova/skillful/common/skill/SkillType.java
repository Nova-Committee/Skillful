package committee.nova.skillful.common.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillType implements ISkillType {
    private final ResourceLocation id;
    private final long maxLevel;
    private final LevelRequiredXp levelRequiredXp;
    @Nullable
    private final LevelChange onLevelChange;
    @Nullable
    private final SkillType.SimpleSkillCheck onCheck;
    @Nullable
    private final SkillType.SimpleSkillCheck onWakeup;

    private SkillType(
            ResourceLocation id,
            int maxLevel,
            LevelRequiredXp levelRequiredXp,
            @Nullable LevelChange onLevelChange,
            @Nullable SkillType.SimpleSkillCheck onCheck,
            @Nullable SkillType.SimpleSkillCheck onWakeup
    ) {
        this.id = id;
        this.maxLevel = maxLevel;
        this.levelRequiredXp = levelRequiredXp;
        this.onLevelChange = onLevelChange;
        this.onCheck = onCheck;
        this.onWakeup = onWakeup;
    }


    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public long getMaxLevel() {
        return maxLevel;
    }

    @Override
    public long getLevelRequiredXp(Player player, long level) {
        return levelRequiredXp.apply(player, level);
    }

    @Override
    public void onLevelChange(Player player, SkillInstance instance, long oldLevel, long newLevel) {
        if (onLevelChange != null) onLevelChange.run(player, instance, oldLevel, newLevel);
    }

    @Override
    public void onCheck(Player player, SkillInstance instance) {
        if (onCheck != null) onCheck.check(player, instance);
    }

    @Override
    public void onWakeup(Player player, SkillInstance instance) {
        if (onWakeup != null) onWakeup.check(player, instance);
    }

    public static class Builder {
        private final ResourceLocation id;
        private int maxLevel = 100;
        private LevelRequiredXp levelRequiredXp = (p, i) -> i * 200;
        private LevelChange onLevelChange = null;
        private SimpleSkillCheck onCheck = null;
        private SimpleSkillCheck onWakeup = null;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public static Builder create(ResourceLocation id) {
            return new Builder(id);
        }

        public Builder maxLevel(int maxLevel) {
            if (maxLevel <= 0) throw new IllegalArgumentException("Max level must be bigger than 0!");
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder levelRequiredXp(@NotNull LevelRequiredXp levelRequiredXp) {
            this.levelRequiredXp = levelRequiredXp;
            return this;
        }

        public Builder onLevelChange(LevelChange onLevelChange) {
            this.onLevelChange = onLevelChange;
            return this;
        }

        public Builder onLogin(SimpleSkillCheck onLogin) {
            this.onCheck = onLogin;
            return this;
        }

        public Builder onWakeup(SimpleSkillCheck onWakeup) {
            this.onWakeup = onWakeup;
            return this;
        }

        public SkillType build() {
            return new SkillType(id, maxLevel, levelRequiredXp, onLevelChange, onCheck, onWakeup);
        }
    }

    @FunctionalInterface
    public interface LevelRequiredXp {
        long apply(Player player, long level);
    }

    @FunctionalInterface
    public interface LevelChange {
        void run(Player player, SkillInstance instance, long oldLevel, long newLevel);
    }

    @FunctionalInterface
    public interface SimpleSkillCheck {
        void check(Player player, SkillInstance instance);
    }
}
