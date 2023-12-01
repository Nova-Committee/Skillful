package committee.nova.skillful.common.cap.skill;

import committee.nova.skillful.common.manager.SkillTypeManager;
import committee.nova.skillful.common.skill.DummySkillInstance;
import committee.nova.skillful.common.skill.ISkillType;
import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Skills {
    public static final Capability<ISkills> SKILLS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static class Impl implements ISkills {
        private final Map<ISkillType, SkillInstance> skills = new HashMap<>();
        private final Map<ResourceLocation, DummySkillInstance> unregisteredSkills = new HashMap<>();

        @Override
        public Collection<SkillInstance> getSkills() {
            return skills.values();
        }

        @Override
        public boolean hasSkill(ISkillType id) {
            return skills.containsKey(id);
        }

        @Override
        public Optional<SkillInstance> getSkill(ISkillType skill) {
            return Optional.ofNullable(skills.get(skill));
        }

        @Override
        public SkillInstance getOrCreateSkill(ISkillType skill) {
            if (hasSkill(skill)) return skills.get(skill);
            final SkillInstance instance = SkillInstance.of(skill);
            skills.put(skill, instance);
            return instance;
        }

        @Override
        public Optional<DummySkillInstance> getUnregisteredSkill(ResourceLocation id) {
            return Optional.ofNullable(unregisteredSkills.get(id));
        }

        @Override
        public ListTag serializeNBT() {
            final ListTag list = new ListTag();
            skills.values().forEach(i -> list.add(i.serializeNBT()));
            unregisteredSkills.values().forEach(i -> list.add(i.serializeNBT()));
            return list;
        }

        @Override
        public void deserializeNBT(ListTag list) {
            skills.clear();
            unregisteredSkills.clear();
            list.forEach(i -> {
                if (!(i instanceof CompoundTag t)) return;
                final ResourceLocation id = ResourceLocation.tryParse(t.getString("skill"));
                if (id == null) return;
                if (SkillTypeManager.hasSkillType(id)) SkillTypeManager.getSkillType(id).ifPresent(s -> {
                    final SkillInstance instance = SkillInstance.of(s);
                    instance.deserializeNBT(t);
                    skills.put(s, instance);
                });
                else {
                    final DummySkillInstance dummy = DummySkillInstance.of(id);
                    dummy.deserializeNBT(t);
                    unregisteredSkills.put(id, dummy);
                }
            });
        }
    }

    public static class Provider implements ICapabilitySerializable<ListTag> {
        private ISkills skills;

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == SKILLS_CAPABILITY ? LazyOptional.of(this::getOrCreateCapability).cast() : LazyOptional.empty();
        }

        public ISkills getOrCreateCapability() {
            if (skills == null) {
                this.skills = new Impl();
            }
            return this.skills;
        }

        @Override
        public ListTag serializeNBT() {
            return getOrCreateCapability().serializeNBT();
        }

        @Override
        public void deserializeNBT(ListTag tag) {
            getOrCreateCapability().deserializeNBT(tag);
        }
    }
}
