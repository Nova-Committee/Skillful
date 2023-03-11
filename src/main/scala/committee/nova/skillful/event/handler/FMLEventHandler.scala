package committee.nova.skillful.event.handler

import committee.nova.skillful.`implicit`.Implicits.EntityPlayerMPImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.play.server.SPacketUpdateBossInfo
import net.minecraft.network.play.server.SPacketUpdateBossInfo.Operation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

object FMLEventHandler {
  def init(): Unit = FMLCommonHandler.instance().bus().register(new FMLEventHandler)
}

class FMLEventHandler {
  @SubscribeEvent
  def onPlayerJoin(event: PlayerLoggedInEvent): Unit = {
    event.player match {
      case mp: EntityPlayerMP =>
        val skills = mp.getSkills
        if (skills.getUUID == null) skills.setUUID(mp.getUniqueID)
      case _ =>
    }
  }

  @SubscribeEvent
  def onPlayerTick(event: PlayerTickEvent): Unit = {
    event.player match {
      case p: EntityPlayerMP => p.getSkills.getSkillInfos.foreach(t => if (t.isActive && t.tick()) p.connection.sendPacket(new SPacketUpdateBossInfo(Operation.REMOVE, t)))
      case _ =>
    }
  }
}
