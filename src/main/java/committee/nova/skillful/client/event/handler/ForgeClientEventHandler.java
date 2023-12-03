package committee.nova.skillful.client.event.handler;

import committee.nova.skillful.client.config.ClientConfig;
import committee.nova.skillful.client.toast.LevelUpToast;
import committee.nova.skillful.common.event.impl.SkillLevelEvent;
import committee.nova.skillful.common.event.impl.SkillXpEvent;
import committee.nova.skillful.common.skill.ISkillType;
import committee.nova.skillful.common.util.Utilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void onLevel(SkillLevelEvent event) {
        if (!event.getPlayer().level.isClientSide) return;
        if (!event.isUp()) return;
        final long newLevel = event.getNewLevel();
        final boolean mastered = newLevel == event.getSkill().getSkill().getMaxLevel();
        final ClientConfig.LvlUpDisplayMode mode = ClientConfig.getLvlUpDisplayMode();
        switch (mode) {
            case TOAST -> Minecraft.getInstance().getToasts()
                    .addToast(LevelUpToast.of(
                            event.getSkill().getSkill().getName(),
                            mastered ?
                                    new TranslatableComponent("toast.skillful.lvlup.mastered").withStyle(ChatFormatting.GOLD) :
                                    new TranslatableComponent("toast.skillful.lvlup.level", String.valueOf(newLevel))
                                            .withStyle(ChatFormatting.DARK_GREEN)));
            case CHAT, OVERLAY -> {
                final Player player = Minecraft.getInstance().player;
                if (player == null) return;
                player.displayClientMessage(Utilities.getSkillInfo4Cmd(player, event.getSkill()),
                        mode.equals(ClientConfig.LvlUpDisplayMode.OVERLAY));
            }
        }

        if (ClientConfig.shouldPlayLvlUpSound()) Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(
                                mastered ? ClientConfig.getLvlUpMasteredSound() : ClientConfig.getLvlUpNormalSound(),
                                1.0F
                        )
                );
    }

    @SubscribeEvent
    public static void onXp(SkillXpEvent event) {
        final ClientConfig.XpChangeDisplayMode mode = ClientConfig.getXpChangeDisplayMode();
        switch (mode) {
            case CHAT, OVERLAY -> {
                final Player player = event.getPlayer();
                if (!player.level.isClientSide) return;
                final ISkillType skillType = event.getSkill().getSkill();
                final long lvlChange = event.getLvlChange();
                final long xpChange = event.getChange();
                final Component msg = new TranslatableComponent(
                        "msg.skillful.xp.change",
                        skillType.getName(),
                        String.valueOf(event.getSkill().getLevel()),
                        String.valueOf(event.getSkill().getXp()),
                        String.valueOf(skillType.getLevelRequiredXp(player, event.getSkill().getLevel())),
                        lvlChange == 0 ?
                                (xpChange > 0 ? "+" + xpChange : String.valueOf(xpChange)) :
                                new TranslatableComponent(lvlChange > 0 ? "msg.skillful.level.up" : "msg.skillful.level.down")
                ).withStyle(getColor(lvlChange, xpChange));
                player.displayClientMessage(msg, mode.equals(ClientConfig.XpChangeDisplayMode.OVERLAY));
            }
        }

    }

    private static ChatFormatting getColor(long lvlChange, long xpChange) {
        if (lvlChange == 0) return xpChange > 0 ?
                ChatFormatting.GREEN : xpChange == 0 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        else return lvlChange > 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
    }
}
