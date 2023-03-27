package committee.nova.skillful.event.handler

import committee.nova.skillful.Skillful
import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.api.skill.{IActOnLevelChange, IXPChangesAfterSleep}
import committee.nova.skillful.event.impl.{SkillLevelEvent, SkillXpEvent}
import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import committee.nova.skillful.player.capabilities.impl.Skills
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.entity.Entity
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemFood
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.text.{Style, TextComponentString, TextComponentTranslation, TextFormatting}
import net.minecraft.util.{ResourceLocation, SoundCategory}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.player.PlayerEvent.Clone
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import java.text.MessageFormat
import scala.collection.mutable

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
  def onXpChanged(event: SkillXpEvent.Post): Unit = event.getPlayer.sendSkillInfo(event.getSkillInstance, event.getAmount)

  @SubscribeEvent
  def onLevelChanged(event: SkillLevelEvent): Unit = {
    val player = event.getPlayer
    val isUp = event.isUp
    event.getSkillInstance.getSkill match {
      case c: IActOnLevelChange => c.act(player, event.getSkillInstance, isUp)
      case _ =>
    }
    if (isUp && event.getSkillInstance.getCurrentLevel != 1) player.connection.sendPacket(new SPacketSoundEffect(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, player.posX, player.posY, player.posZ, 1.0F, 1.0F))
  }

  @SubscribeEvent
  def onWakeUp(event: PlayerWakeUpEvent): Unit = {
    if (!(event.shouldSetSpawn() && !event.updateWorld() && !event.wakeImmediately())) return
    val buffer = new mutable.ArrayBuffer[(ResourceLocation, Int)]
    event.getEntityPlayer match {
      case p: EntityPlayerMP =>
        p.getSkills.getSkills.foreach(i => {
          i.getSkill match {
            case x: IXPChangesAfterSleep =>
              val v = x.change(p, i)
              if (v != 0) {
                i.addXp(p, x.change(p, i))
                buffer.+=((x.getId, v))
              }
            case _ =>
          }
        })
        if (buffer.nonEmpty) {
          p.sendMessage(new TextComponentTranslation("info.skillful.wakeup.summary").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)))
          buffer.foreach(x => {
            val c = x._2
            p.sendMessage(new TextComponentString(MessageFormat.format(
              new TextComponentTranslation(s"info.skillful.wakeup.${if (c > 0) "increase" else "decrease"}").getFormattedText,
              new TextComponentTranslation(s"skill.${x._1.getNamespace}.${x._1.getPath}").getFormattedText,
              c.abs.toString
            )).setStyle(new Style().setColor(if (c > 0) TextFormatting.GREEN else TextFormatting.RED)))
          })
        }
      case _ =>
    }
  }

  @SubscribeEvent
  def onFoodEaten(e: LivingEntityUseItemEvent.Finish): Unit = {
    if (!e.getEntityLiving.isInstanceOf[EntityPlayerMP]) return
    val stack = e.getItem
    stack.getItem match {
      case _: ItemFood => SkillfulStorage.applyFoodEffect(e.getEntityLiving.asInstanceOf[EntityPlayerMP], stack)
      case _ =>
    }
  }
}


