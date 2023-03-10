package committee.nova.skillful.event.handler

import committee.nova.skillful.Skillful
import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.`implicit`.Implicits.EntityPlayerMPImplicit
import committee.nova.skillful.event.impl.SkillXpEvent
import committee.nova.skillful.player.capabilities.Skills
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.SPacketUpdateBossInfo
import net.minecraft.network.play.server.SPacketUpdateBossInfo.Operation
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent.Clone
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.server.FMLServerHandler

import scala.util.Try

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

  @SubscribeEvent
  def onXpChanged(event: SkillXpEvent.Post): Unit = {
    if (event.getAmount < 0) return
    Try(FMLServerHandler.instance().getServer.getPlayerList.getPlayerByUUID(event.getPlayerUUID)).toOption.foreach(p => {
      val instance = event.getSkillInstance
      val info = p.getSkillInfo(instance.getSkill.getId)
      info.setPercent(instance.getCurrentXp * 1F / instance.getSkill.getLevelRequiredXp(instance.getCurrentLevel))
      info.activate()
      p.connection.sendPacket(new SPacketUpdateBossInfo(Operation.ADD, info))
    })
  }
}


