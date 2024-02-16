package committee.nova.skillful.event.handler

import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.{PlayerChangedDimensionEvent, PlayerLoggedInEvent, PlayerRespawnEvent}

object FMLEventHandler {
  def init(): Unit = FMLCommonHandler.instance().bus().register(new FMLEventHandler)
}

class FMLEventHandler {
  @SubscribeEvent
  def onPlayerLogin(event: PlayerLoggedInEvent): Unit = {
    event.player match {
      case _: FakePlayer =>
      case p: EntityPlayerMP =>
        p.applySkillAttrs()
        p.syncSkills()
      case _ =>
    }
  }

  @SubscribeEvent
  def onPlayerChangedDimension(event: PlayerChangedDimensionEvent): Unit = {
    if (event.player.isFake) return
    event.player.syncSkills()
    event.player.applySkillAttrs()
  }

  @SubscribeEvent
  def onPlayerRespawn(event: PlayerRespawnEvent): Unit = {
    if (event.player.isFake) return
    event.player.syncSkills()
    event.player.applySkillAttrs()
  }
}
