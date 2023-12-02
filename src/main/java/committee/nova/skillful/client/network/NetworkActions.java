package committee.nova.skillful.client.network;

import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.event.impl.SkillLevelEvent;
import committee.nova.skillful.common.event.impl.SkillXpEvent;
import committee.nova.skillful.common.manager.SkillTypeManager;
import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public class NetworkActions {
    public static void syncSingleSkill(ResourceLocation id, long level, long xp) {
        final Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!SkillTypeManager.hasSkillType(id)) return;
        player.getCapability(Skills.SKILLS_CAPABILITY)
                .ifPresent(s -> SkillTypeManager.getSkillType(id).ifPresent(t -> {
                            final SkillInstance instance = s.getOrCreateSkill(t);
                            final long level0 = instance.getLevel();
                            final long xp0 = instance.getXp();
                            long levelChange = 0;
                            instance.setLevel(level);
                            instance.setXp(xp);
                            if (level0 != level) {
                                levelChange = level - level0;
                                MinecraftForge.EVENT_BUS.post(new SkillLevelEvent(player, instance, level0, level));
                            }
                            if (xp0 != xp) {
                                MinecraftForge.EVENT_BUS.post(new SkillXpEvent(player, instance, xp - xp0, levelChange));
                            }
                        })
                );
    }

    public static void syncSkills(ListTag tag) {
        final Player player = Minecraft.getInstance().player;
        if (player == null) return;
        player.getCapability(Skills.SKILLS_CAPABILITY)
                .ifPresent(s -> s.deserializeNBT(tag));
    }
}
