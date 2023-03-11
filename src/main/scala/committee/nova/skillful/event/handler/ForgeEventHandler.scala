package committee.nova.skillful.event.handler

import committee.nova.skillful.Skillful
import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.event.impl.{SkillLevelEvent, SkillXpEvent}
import committee.nova.skillful.implicits.Implicits.EntityPlayerMPImplicit
import committee.nova.skillful.player.capabilities.Skills
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.{ResourceLocation, SoundCategory}
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

  @SubscribeEvent
  def onXpChanged(event: SkillXpEvent.Post): Unit = {
    if (event.getAmount < 0) return
    val p = event.getPlayer
    val instance = event.getSkillInstance
    val info = p.getSkillInfo(instance.getSkill.getId)
    info.addPlayer(p)
    info.setPercent(instance.getCurrentXp * 1F / instance.getSkill.getLevelRequiredXp(instance.getCurrentLevel))
    info.activate()
  }

  @SubscribeEvent
  def onLevelUp(event: SkillLevelEvent.Up): Unit = {
    val player = event.getPlayer
    player.connection.sendPacket(new SPacketSoundEffect(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, player.posX, player.posY, player.posZ, 1.0F, 1.0F))
  }
}


