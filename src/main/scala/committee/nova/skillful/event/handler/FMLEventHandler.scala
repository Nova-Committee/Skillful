package committee.nova.skillful.event.handler

import committee.nova.skillful.`implicit`.Implicits.EntityPlayerMPImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{Phase, PlayerTickEvent}

object FMLEventHandler {
  def init(): Unit = FMLCommonHandler.instance().bus().register(new FMLEventHandler)
}

class FMLEventHandler {
  @SubscribeEvent
  def onPlayerTick(event: PlayerTickEvent): Unit = {
    if (event.phase == Phase.START) return
    event.player match {
      case p: EntityPlayerMP => p.getSkills.getSkillInfos.foreach(t => if (t.isActive && t.tick()) t.removePlayer(p))
      case _ =>
    }
  }
}
