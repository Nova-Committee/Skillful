package committee.nova.skillful.network.handler

import committee.nova.skillful.Constants
import committee.nova.skillful.network.message.{InfoClearMessage, SkillsSyncMessage}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.simple.SimpleChannel
import net.minecraftforge.fml.network.{NetworkEvent, NetworkRegistry}

import java.util.function.Supplier

object NetworkHandler {
  final val version = "1.0"
  var instance: SimpleChannel = _
  private var id: Int = 0

  private def nextId: Int = {
    id += 1
    id
  }

  def registerMessage(): Unit = {
    instance = NetworkRegistry.newSimpleChannel(
      new ResourceLocation(Constants.MODID, Constants.MODID),
      () => version,
      v => v.equals(version),
      v => v.equals(version)
    )
    instance.messageBuilder(classOf[InfoClearMessage], nextId)
      .encoder((_, _) => {})
      .decoder(_ => new InfoClearMessage)
      .consumer((m: InfoClearMessage, ctx: Supplier[NetworkEvent.Context]) => m.handler(ctx))
      .add()
    instance.messageBuilder(classOf[SkillsSyncMessage], nextId)
      .encoder((m, p) => m.toBytes(p))
      .decoder(p => new SkillsSyncMessage(p))
      .consumer((m: SkillsSyncMessage, ctx: Supplier[NetworkEvent.Context]) => m.handler(ctx))
      .add()
  }
}
