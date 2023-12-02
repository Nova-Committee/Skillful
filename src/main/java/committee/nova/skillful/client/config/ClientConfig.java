package committee.nova.skillful.client.config;

import committee.nova.skillful.Skillful;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Skillful.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientConfig {
    public static final ForgeConfigSpec CFG;
    private static final ForgeConfigSpec.EnumValue<LvlUpDisplayMode> _lvlUpDisplayMode;
    private static final ForgeConfigSpec.EnumValue<XpChangeDisplayMode> _xpChangeDisplayMode;
    private static final ForgeConfigSpec.LongValue _toastExpiration;
    private static final ForgeConfigSpec.BooleanValue _toastDarkMode;
    private static final ForgeConfigSpec.BooleanValue _playLvlUpSound;
    private static final ForgeConfigSpec.ConfigValue<String> _lvlUpNormalSound;
    private static final ForgeConfigSpec.ConfigValue<String> _lvlUpMasteredSound;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Skillful Client Settings").push("Display");
        _lvlUpDisplayMode = builder.defineEnum("lvlUpDisplayMode", LvlUpDisplayMode.TOAST);
        _xpChangeDisplayMode = builder.defineEnum("xpChangeDisplayMode", XpChangeDisplayMode.OVERLAY);
        builder.push("Toast");
        _toastExpiration = builder.defineInRange("toastExpiration", 3000L, 500L, 10000L);
        _toastDarkMode = builder.define("toastDarkMode", false);
        builder.pop();
        builder.pop();
        builder.push("Sound");
        _playLvlUpSound = builder.define("playLvlUpSound", true);
        _lvlUpNormalSound = builder.define("lvlUpNormalSound", "minecraft:entity.player.levelup");
        _lvlUpMasteredSound = builder.define("lvlUpMasteredSound", "minecraft:ui.toast.challenge_complete");
        builder.pop();
        CFG = builder.build();
    }

    private static LvlUpDisplayMode lvlUpDisplayMode = LvlUpDisplayMode.TOAST;
    private static XpChangeDisplayMode xpChangeDisplayMode = XpChangeDisplayMode.OVERLAY;
    private static long toastExpiration = 3000L;
    private static boolean toastDarkMode = false;
    private static boolean playLvlUpSound = true;
    private static SoundEvent lvlUpNormalSound = SoundEvents.PLAYER_LEVELUP;
    private static SoundEvent lvlUpMasteredSound = SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        lvlUpDisplayMode = _lvlUpDisplayMode.get();
        xpChangeDisplayMode = _xpChangeDisplayMode.get();
        toastExpiration = _toastExpiration.get();
        toastDarkMode = _toastDarkMode.get();
        playLvlUpSound = _playLvlUpSound.get();
        final ResourceLocation normalId = ResourceLocation.tryParse(_lvlUpNormalSound.get());
        lvlUpNormalSound = (normalId == null || !ForgeRegistries.SOUND_EVENTS.containsKey(normalId)) ?
                SoundEvents.PLAYER_LEVELUP :
                ForgeRegistries.SOUND_EVENTS.getValue(normalId);
        final ResourceLocation masteredId = ResourceLocation.tryParse(_lvlUpMasteredSound.get());
        lvlUpMasteredSound = (masteredId == null || !ForgeRegistries.SOUND_EVENTS.containsKey(masteredId)) ?
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE :
                ForgeRegistries.SOUND_EVENTS.getValue(masteredId);
    }

    public static boolean shouldPlayLvlUpSound() {
        return playLvlUpSound;
    }

    public static LvlUpDisplayMode getLvlUpDisplayMode() {
        return lvlUpDisplayMode;
    }

    public static XpChangeDisplayMode getXpChangeDisplayMode() {
        return xpChangeDisplayMode;
    }

    public static long getToastExpiration() {
        return toastExpiration;
    }

    public static boolean isToastDarkModeOn() {
        return toastDarkMode;
    }

    public static SoundEvent getLvlUpNormalSound() {
        return lvlUpNormalSound;
    }

    public static SoundEvent getLvlUpMasteredSound() {
        return lvlUpMasteredSound;
    }

    public enum LvlUpDisplayMode {
        TOAST,
        OVERLAY,
        CHAT,
        DISABLED
    }

    public enum XpChangeDisplayMode {
        OVERLAY,
        CHAT,
        DISABLED
    }
}
