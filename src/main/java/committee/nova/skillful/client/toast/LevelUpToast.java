package committee.nova.skillful.client.toast;

import committee.nova.skillful.Skillful;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class LevelUpToast implements Toast {
    private static final ResourceLocation texture = new ResourceLocation(Skillful.MODID, "textures/gui/toasts.png");
    private static final Component title = Component.translatable("toast.skillful.lvlup.title");
    private final long expiration = 3000L; // TODO: Config
    private final Component message;

    private LevelUpToast(Component skillName, long newLevel) {
        this.message = Component.translatable("toast.skillful.lvlup.desc", skillName, String.valueOf(newLevel));
    }

    public static LevelUpToast of(Component skillName, long newLevel) {
        return new LevelUpToast(skillName, newLevel);
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent manager, long timeElapsed) {
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.blit(texture, 0, 0, 0, 0, width(), height());
        graphics.blit(texture, 8, 0, 241, 0, 15, 30);
        graphics.drawString(manager.getMinecraft().font, title, 35, 7, Color.WHITE.getRGB());
        graphics.drawString(manager.getMinecraft().font, message, 35, 18, Color.WHITE.getRGB());
        return timeElapsed >= expiration ? Visibility.HIDE : Visibility.SHOW;
    }


}
