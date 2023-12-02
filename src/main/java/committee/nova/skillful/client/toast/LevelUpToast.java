package committee.nova.skillful.client.toast;

import committee.nova.skillful.Skillful;
import committee.nova.skillful.client.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class LevelUpToast implements Toast {
    private static final ResourceLocation texture = new ResourceLocation(Skillful.MODID, "textures/gui/toasts.png");
    private static final Component title = Component.translatable("toast.skillful.lvlup.title");
    private final long expiration = ClientConfig.getToastExpiration();
    private final Component message;

    private LevelUpToast(Component skillName, Component newLevel) {
        this.message = Component.translatable("toast.skillful.lvlup.desc", skillName, newLevel);
    }

    public static LevelUpToast of(Component skillName, Component newLevel) {
        return new LevelUpToast(skillName, newLevel);
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent manager, long timeElapsed) {
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.blit(texture, 0, 0, 0, ClientConfig.isToastDarkModeOn() ? 0 : 32, width(), height());
        graphics.blit(texture, 8, 0, 241, 0, 15, 30);
        final int color = ClientConfig.isToastDarkModeOn() ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB();
        graphics.drawString(manager.getMinecraft().font, title, 35, 7, color, false);
        graphics.drawString(manager.getMinecraft().font, message, 35, 18, color, false);
        return timeElapsed >= expiration ? Visibility.HIDE : Visibility.SHOW;
    }
}
