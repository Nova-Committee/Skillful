package committee.nova.skillful.client.event.handler;

import committee.nova.skillful.client.toast.LevelUpToast;
import committee.nova.skillful.common.event.impl.SkillLevelEvent;
import committee.nova.skillful.common.event.impl.SkillXpEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void onXp(SkillXpEvent event) {
        // TODO
    }

    @SubscribeEvent
    public static void onLevel(SkillLevelEvent event) {
        if (!event.getPlayer().level().isClientSide) return;
        if (!event.isUp()) return;
        Minecraft.getInstance().getToasts()
                .addToast(LevelUpToast.of(event.getSkill().getSkill().getName(), event.getNewLevel()));
    }
}
