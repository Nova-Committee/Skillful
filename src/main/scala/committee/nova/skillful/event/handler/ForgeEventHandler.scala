package committee.nova.skillful.event.handler

import committee.nova.skillful.Skillful
import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.player.capabilities.Skills
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent.Clone
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ForgeEventHandler {
  def init(): Unit = MinecraftForge.EVENT_BUS.register(new ForgeEventHandler)
}

class ForgeEventHandler {
  @SubscribeEvent
  def onClone(event: Clone): Unit = {
    val oldPlayer = event.getOriginal
    val newPlayer = event.getEntityPlayer
    val cap = skillfulCap
    val storage = skillfulCap.getStorage
    if (!(oldPlayer.hasCapability(cap, null) && newPlayer.hasCapability(cap, null))) return
    storage.readNBT(cap, newPlayer.getCapability(cap, null), null, storage.writeNBT(cap, oldPlayer.getCapability(cap, null), null))
  }

  @SubscribeEvent
  def onAttachCaps(event: AttachCapabilitiesEvent[Entity]): Unit = {
    event.getObject match {
      case _: EntityPlayer => event.addCapability(new ResourceLocation(Skillful.MODID, Skillful.MODID), new Skills.Provider)
      case _ =>
    }
  }
}


