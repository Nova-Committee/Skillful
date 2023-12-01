package committee.nova.skillful.common.test;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.manager.SkillTypeManager;
import committee.nova.skillful.common.skill.ISkillType;
import committee.nova.skillful.common.skill.SkillType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TestManager {
    private static final ISkillType test = SkillType.Builder
            .create(new ResourceLocation(Skillful.MODID, "test"))
            .maxLevel(100)
            .levelRequiredXp((p, l) -> l * 2)
            .build();

    public TestManager() {
        SkillTypeManager.registerSkillType(test);
    }

    @SubscribeEvent
    public void onHit(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.getCapability(Skills.SKILLS_CAPABILITY).ifPresent(c -> c.getOrCreateSkill(test).changeXp(player, 10));
    }
}
