package committee.nova.skillful.event.handler

import committee.nova.skillful.api.ICheckOnLogin
import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.{PlayerChangedDimensionEvent, PlayerLoggedInEvent, PlayerRespawnEvent}
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

  @SubscribeEvent
  def onPlayerLogin(event: PlayerLoggedInEvent): Unit = {
    event.player match {
      case p: EntityPlayerMP => {
        p.getSkills.getSkills.foreach(i => i.getSkill match {
          case l: ICheckOnLogin => l.check(p, i)
          case _ =>
        })
        p.syncSkills()
      }
      case _ =>
    }
  }

  @SubscribeEvent
  def onPlayerChangedDimension(event: PlayerChangedDimensionEvent): Unit = event.player.syncSkills()

  @SubscribeEvent
  def onPlayerRespawn(event: PlayerRespawnEvent): Unit = event.player.syncSkills()
}
