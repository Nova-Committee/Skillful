package committee.nova.skillful.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import committee.nova.skillful.Skillful;
import committee.nova.skillful.client.config.ClientConfig;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class LevelUpToast implements Toast {
    private static final ResourceLocation texture = new ResourceLocation(Skillful.MODID, "textures/gui/toasts.png");
    private static final Component title = new TranslatableComponent("toast.skillful.lvlup.title");
    private final long expiration = ClientConfig.getToastExpiration();
    private final Component message;

    private LevelUpToast(Component skillName, Component newLevel) {
        this.message = new TranslatableComponent("toast.skillful.lvlup.desc", skillName, newLevel);
    }

    public static LevelUpToast of(Component skillName, Component newLevel) {
        return new LevelUpToast(skillName, newLevel);
    }

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent manager, long timeElapsed) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(poseStack, 0, 0, 0, ClientConfig.isToastDarkModeOn() ? 0 : 32,
                width(), height(), 256, 256);
        GuiComponent.blit(poseStack, 8, 0, 241, 0, 15, 30, 256, 256);
        final int color = ClientConfig.isToastDarkModeOn() ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB();
        manager.getMinecraft().font.draw(poseStack, title, 35, 7, color);
        manager.getMinecraft().font.draw(poseStack, message, 35, 18, color);
        return timeElapsed >= expiration ? Visibility.HIDE : Visibility.SHOW;
    }
}
