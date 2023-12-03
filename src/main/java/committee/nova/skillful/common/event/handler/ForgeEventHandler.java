package committee.nova.skillful.common.event.handler;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.cap.skill.ISkills;
import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.command.CommandManager;
import committee.nova.skillful.common.util.Utilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onRegisterCap(RegisterCapabilitiesEvent event) {
        event.register(Skills.Impl.class);
    }

    @SubscribeEvent
    public static void onAttachCap(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player player)) return;
        if (player instanceof FakePlayer) return;
        event.addCapability(new ResourceLocation(Skillful.MODID, "skills"), new Skills.Provider());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        final Player original = event.getOriginal();
        original.reviveCaps();
        final LazyOptional<ISkills> oldCap = original.getCapability(Skills.SKILLS_CAPABILITY);
        final LazyOptional<ISkills> newCap = event.getEntity().getCapability(Skills.SKILLS_CAPABILITY);
        newCap.ifPresent((n) -> oldCap.ifPresent((o) -> n.deserializeNBT(o.serializeNBT())));
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        checkAndSync(event.getPlayer());
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        checkAndSync(event.getPlayer());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        checkAndSync(event.getPlayer());
    }

    @SubscribeEvent
    public static void onWakeup(PlayerWakeUpEvent event) {
        if (event.updateWorld() || event.wakeImmediately()) return;
        if (!(event.getEntity() instanceof ServerPlayer p) || p instanceof FakePlayer) return;
        p.displayClientMessage(new TranslatableComponent("msg.skillful.sleep")
                .withStyle(ChatFormatting.AQUA), false);
        p.getCapability(Skills.SKILLS_CAPABILITY)
                .ifPresent(i -> i.getSkills().forEach(s -> s.getSkill().onWakeup(p, s)));
    }

    @SubscribeEvent
    public static void onRegisterCmd(RegisterCommandsEvent event) {
        CommandManager.init(event.getDispatcher());
    }

    private static void checkAndSync(Player player) {
        if (!(player instanceof ServerPlayer s) || s instanceof FakePlayer) return;
        Utilities.checkSkills(s);
        Utilities.syncSkills(s);
    }
}
