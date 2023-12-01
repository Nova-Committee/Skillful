package committee.nova.skillful.common.manager;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.skill.ISkillType;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SkillTypeManager {
    private static final Map<ResourceLocation, ISkillType> skillTypes = new HashMap<>();

    public static boolean registerSkillType(ISkillType skill) {
        final ResourceLocation id = skill.getId();
        if (skillTypes.containsKey(id)) {
            Skillful.LOGGER.error("Duplicate skill \"{}\"!", id.toString());
            return false;
        }
        skillTypes.put(id, skill);
        Skillful.LOGGER.info("Successfully registered skill \"{}\"!", id.toString());
        return true;
    }

    public static boolean hasSkillType(ResourceLocation id) {
        return skillTypes.containsKey(id);
    }

    public static Optional<ISkillType> getSkillType(ResourceLocation id) {
        return Optional.ofNullable(skillTypes.get(id));
    }

    public static Collection<ISkillType> getSkillTypes() {
        return skillTypes.values();
    }
}
