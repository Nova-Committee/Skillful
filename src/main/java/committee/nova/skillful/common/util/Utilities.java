package committee.nova.skillful.common.util;

import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.network.handler.NetworkHandler;
import committee.nova.skillful.common.network.msg.SyncSkillsMsg;
import committee.nova.skillful.common.skill.SkillInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public static boolean isPlayerFake(ServerPlayer player) {
        return player instanceof FakePlayer;
    }

    public static void syncSkills(ServerPlayer player) {
        player.getCapability(Skills.SKILLS_CAPABILITY).ifPresent(s -> {
            NetworkHandler.getInstance().send(PacketDistributor.PLAYER.with(() -> player), new SyncSkillsMsg(s.serializeNBT()));
        });
    }

    public static void checkSkills(ServerPlayer player) {
        player.getCapability(Skills.SKILLS_CAPABILITY)
                .ifPresent(s -> s.getSkills().forEach(i -> i.getSkill().onCheck(player, i)));
    }

    public static List<Component> getSkillList4Cmd(ServerPlayer player) {
        final List<Component> list = new ArrayList<>();
        player.getCapability(Skills.SKILLS_CAPABILITY).ifPresent(s -> {
            if (s.getSkills().isEmpty()) {
                list.add(Component.translatable("cmd.skillful.skill.empty").withStyle(ChatFormatting.GRAY));
                return;
            }
            list.add(Component.translatable("cmd.skillful.skill.list", player.getName()).withStyle(ChatFormatting.YELLOW));
            list.addAll(s.getSkills().stream().map(i -> getSkillInfo4Cmd(player, i)).toList());
        });
        return list;
    }

    public static Component getSkillInfo4Cmd(Player player, SkillInstance i) {
        final Component name = i.getSkill().getName();
        if (i.clueless()) return Component.translatable("msg.skillful.level.clueless", name)
                .withStyle(ChatFormatting.GRAY);
        if (i.mastered()) return Component.translatable("msg.skillful.level.mastered", name)
                .withStyle(ChatFormatting.GOLD);
        return Component.translatable(
                "msg.skillful.level.xp",
                name,
                i.getLevel(),
                i.getXp(),
                i.getSkill().getLevelRequiredXp(player, i.getLevel())
        );
    }
}
