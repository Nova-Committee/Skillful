package committee.nova.skillful.common.skill;

import net.minecraft.resources.ResourceLocation;

public class DummySkillInstance implements ISkillRecord {
    private final ResourceLocation id;
    private long level;
    private long xp;

    private DummySkillInstance(ResourceLocation id) {
        this.id = id;
    }

    public static DummySkillInstance of(ResourceLocation id) {
        return new DummySkillInstance(id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
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
