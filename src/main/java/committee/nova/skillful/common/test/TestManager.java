package committee.nova.skillful.common.test;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.manager.SkillTypeManager;
import committee.nova.skillful.common.skill.ISkillType;
import committee.nova.skillful.common.skill.SkillType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class TestManager {
    private static AttributeModifier speedMod(double v) {
        return new AttributeModifier(speedModUniqueId, "test speed", v, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    ;
    private static final UUID speedModUniqueId = UUID.fromString("B16DB2BB-6E96-32F0-42C9-7E4345FAE804");
    private static final ISkillType test = SkillType.Builder
            .create(new ResourceLocation(Skillful.MODID, "test"))
            .maxLevel(100)
            .levelRequiredXp((p, l) -> l * 2)
            .onCheck((p, i) -> {
                final var a = p.getAttribute(Attributes.MOVEMENT_SPEED);
                if (a == null) return;
                a.removeModifier(speedModUniqueId);
                a.addTransientModifier(speedMod(i.getLevel() * 1.0 / i.getSkill().getMaxLevel()));
            })
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
