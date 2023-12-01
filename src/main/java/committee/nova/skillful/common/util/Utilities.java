package committee.nova.skillful.common.util;

import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.network.handler.NetworkHandler;
import committee.nova.skillful.common.network.msg.SyncSkillsMsg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;

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
}
