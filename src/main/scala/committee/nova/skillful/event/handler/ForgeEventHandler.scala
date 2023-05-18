package committee.nova.skillful.event.handler

import committee.nova.skillful.Constants
import committee.nova.skillful.command.init.CommandInit
import committee.nova.skillful.event.handler.CapabilityHandler.skillfulCap
import committee.nova.skillful.event.impl.{SkillLevelEvent, SkillXpEvent}
import committee.nova.skillful.implicits.Implicits.PlayerEntityImplicit
import committee.nova.skillful.player.capabilities.impl.Skills
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.entity.Entity
import net.minecraft.entity.player.{PlayerEntity, ServerPlayerEntity}
import net.minecraft.network.play.server.SPlaySoundEffectPacket
import net.minecraft.util.text.{TextFormatting, TranslationTextComponent}
import net.minecraft.util.{ResourceLocation, SoundCategory, SoundEvents, Util}
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.event.TickEvent.Phase
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.player.PlayerEvent.{Clone, PlayerChangedDimensionEvent, PlayerLoggedInEvent, PlayerRespawnEvent}
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent
import net.minecraftforge.event.{AttachCapabilitiesEvent, RegisterCommandsEvent, TickEvent}
import net.minecraftforge.eventbus.api.SubscribeEvent

import scala.collection.mutable

class ForgeEventHandler {
  @SubscribeEvent
  def onAttachCaps(event: AttachCapabilitiesEvent[Entity]): Unit = {
    event.getObject match {
      case _: PlayerEntity => event.addCapability(new ResourceLocation(Constants.MODID, Constants.MODID), new Skills.Provider)
      case _ =>
    }
  }

  @SubscribeEvent
  def onRegisterCommand(e: RegisterCommandsEvent): Unit = CommandInit.init(e.getDispatcher)

  @SubscribeEvent
  def onPlayerTick(event: TickEvent.PlayerTickEvent): Unit = {
    if (event.phase == Phase.START) return
    event.player match {
      case _: FakePlayer =>
      case p: ServerPlayerEntity => p.getSkills.getSkillInfos.foreach(t => if (t.isActive && t.tick()) t.removePlayer(p))
      case _ =>
    }
  }

  @SubscribeEvent
  def onPlayerLogin(event: PlayerLoggedInEvent): Unit = {
    event.getPlayer match {
      case _: FakePlayer =>
      case p: ServerPlayerEntity =>
        p.applySkillAttrs()
        p.syncSkills()
      case _ =>
    }
  }

  @SubscribeEvent
  def onPlayerChangedDimension(event: PlayerChangedDimensionEvent): Unit = {
    if (event.getPlayer.isFake) return
    event.getPlayer.clearSkillInfoCache()
    event.getPlayer.syncSkills()
    event.getPlayer.applySkillAttrs()
  }

  @SubscribeEvent
  def onPlayerRespawn(event: PlayerRespawnEvent): Unit = {
    if (event.getPlayer.isFake) return
    event.getPlayer.clearSkillInfoCache()
    event.getPlayer.syncSkills()
    event.getPlayer.applySkillAttrs()
  }

  @SubscribeEvent
  def onClone(event: Clone): Unit = {
    val oldPlayer = event.getOriginal
    if (oldPlayer.isFake) return
    val newPlayer = event.getPlayer
    val cap = skillfulCap
    val storage = skillfulCap.getStorage
    oldPlayer.getCapability(cap).ifPresent(o => newPlayer.getCapability(cap).ifPresent(n =>
      storage.readNBT(cap, n, null, storage.writeNBT(cap, o, null))
    ))
  }

  @SubscribeEvent
  def onXpChanged(event: SkillXpEvent.Post): Unit = if (!event.getPlayer.isFake) event.getPlayer.sendSkillInfo(event.getSkillInstance, event.getAmount)

  @SubscribeEvent
  def onLevelChanged(event: SkillLevelEvent): Unit = {
    val player = event.getPlayer
    if (player.isFake) return
    val isUp = event.isUp
    event.getSkillInstance.getSkill match {
      case c if c.shouldActOnLevelChange => c.actOnLevelChange(player, event.getSkillInstance, isUp)
      case _ =>
    }
    if (isUp && event.getSkillInstance.getCurrentLevel != 1)
      player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,
        player.getPosX, player.getPosY, player.getPosZ, 1.0F, 1.0F))
  }

  @SubscribeEvent
  def onWakeUp(event: PlayerWakeUpEvent): Unit = {
    if (!(!event.updateWorld() && !event.wakeImmediately())) return
    val player = event.getPlayer
    if (player.isFake) return
    val buffer = new mutable.ArrayBuffer[(ResourceLocation, Int)]
    player match {
      case p: ServerPlayerEntity =>
        p.getSkills.getSkills.foreach(i => {
          i.getSkill match {
            case x if x.shouldChangeXPAfterSleep =>
              val v = x.changeXPAfterSleep(p, i)
              if (v != 0) {
                i.addXp(p, v)
                buffer.+=((x.getId, v))
              }
            case _ =>
          }
        })
        if (buffer.nonEmpty) {
          p.sendMessage(new TranslationTextComponent("info.skillful.wakeup.summary").mergeStyle(TextFormatting.LIGHT_PURPLE), Util.DUMMY_UUID)
          buffer.foreach(x => {
            val c = x._2
            p.sendMessage(new TranslationTextComponent(s"info.skillful.wakeup.${if (c > 0) "increase" else "decrease"}",
              new TranslationTextComponent(s"skill.${x._1.getNamespace}.${x._1.getPath}"),
              c.abs.toString
            ).mergeStyle(if (c > 0) TextFormatting.GREEN else TextFormatting.RED), Util.DUMMY_UUID)
          })
        }
      case _ =>
    }
  }

  @SubscribeEvent
  def onFoodEaten(e: LivingEntityUseItemEvent.Finish): Unit = {
    if (!e.getEntityLiving.isInstanceOf[ServerPlayerEntity] || e.getEntityLiving.isInstanceOf[FakePlayer]) return
    val stack = e.getItem
    SkillfulStorage.applyFoodEffect(e.getEntityLiving.asInstanceOf[ServerPlayerEntity], stack)
  }
}
