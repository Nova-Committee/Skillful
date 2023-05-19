package committee.nova.skillful.network.message

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.network.NetworkEvent

import java.util.function.Supplier

class InfoClearMessage {
  def handler(ctx: Supplier[NetworkEvent.Context]): Unit = {
    ctx.get().enqueueWork(() => Minecraft.getInstance().gui.getBossOverlay.reset())
    ctx.get().setPacketHandled(true)
  }
}
