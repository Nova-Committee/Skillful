package committee.nova.skillful.common.cap.skill;

import committee.nova.skillful.common.skill.DummySkillInstance;
import committee.nova.skillful.common.skill.ISkillType;
import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Optional;

public interface ISkills extends INBTSerializable<ListTag> {
    Collection<SkillInstance> getSkills();

    boolean hasSkill(ISkillType skill);

    Optional<SkillInstance> getSkill(ISkillType skill);

    SkillInstance getOrCreateSkill(ISkillType skill);

    Optional<DummySkillInstance> getUnregisteredSkill(ResourceLocation id);

    void removeUnregisteredSkill(ResourceLocation id);
}
