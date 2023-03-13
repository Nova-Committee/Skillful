package committee.nova.skillful.network.handler

import committee.nova.skillful.Skillful
import committee.nova.skillful.network.message.{InfoClearMessage, SkillsSyncMessage}
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side

object MessageHandler {
  class SkillsSyncHandler extends IMessageHandler[SkillsSyncMessage, IMessage] {
    override def onMessage(message: SkillsSyncMessage, ctx: MessageContext): IMessage = {
      if (ctx.side != Side.CLIENT) return null
      val tag = message.getTag.getTag("skills")
      Minecraft.getMinecraft.addScheduledTask(new Runnable {
        override def run(): Unit = {
          val player = Minecraft.getMinecraft.player
          if (!player.hasCapability(Skillful.skillfulCap, null)) return
          val skills = player.getCapability(Skillful.skillfulCap, null)
          val storage = Skillful.skillfulCap.getStorage
          storage.readNBT(Skillful.skillfulCap, skills, null, tag)
        }
      })
      null
    }
  }

  class InfoClearHandler extends IMessageHandler[InfoClearMessage, IMessage] {
    override def onMessage(message: InfoClearMessage, ctx: MessageContext): IMessage = {
      if (ctx.side != Side.CLIENT) return null
      Minecraft.getMinecraft.addScheduledTask(new Runnable {
        override def run(): Unit = {
          Minecraft.getMinecraft.ingameGUI.getBossOverlay.clearBossInfos()
        }
      })
      null
    }
  }
}
