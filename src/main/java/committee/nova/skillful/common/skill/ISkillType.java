package committee.nova.skillful.common.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface ISkillType {
    ResourceLocation getId();

    long getMaxLevel();

    long getLevelRequiredXp(Player player, long level);

    void onLevelChange(Player player, SkillInstance instance, long oldLevel, long newLevel);

    void onCheck(Player player, SkillInstance instance);

    void onWakeup(Player player, SkillInstance instance);

    default Component getName() {
        return new TranslatableComponent("type.skillful." + getId().toString().replace(':', '.'));
    }
}
