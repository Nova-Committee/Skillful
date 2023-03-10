package committee.nova.skillful.event.handler

import committee.nova.skillful.`implicit`.Implicits.EntityPlayerMPImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent

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
    }
  }
}
