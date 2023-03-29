package committee.nova.skillful.util

import committee.nova.skillful.Skillful
import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.network.message.SkillsSyncMessage
import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.info.SkillInfo
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text._

import java.text.MessageFormat

object Utilities {
  def getPlayerSkills(player: EntityPlayer): ISkills = player.getCapability(Skillful.skillfulCap, null)

  def getPlayerSkillStat(player: EntityPlayer, skill: ISkill): SkillInstance = getPlayerSkills(player).getSkill(skill)

  def getPlayerSkillStat(player: EntityPlayer, id: ResourceLocation): SkillInstance = getPlayerSkills(player).getSkill(id)

  def getPlayerSkillStatStrictly(player: EntityPlayer, id: ResourceLocation): Option[SkillInstance] = getPlayerSkills(player).getSkillStrictly(id)

  def getPlayerSkillStatCleanly(player: EntityPlayer, id: ResourceLocation): Option[SkillInstance] = getPlayerSkills(player).getSkillCleanly(id)

  def getPlayerSkillStatCleanly(player: EntityPlayer, skill: ISkill): Option[SkillInstance] = getPlayerSkills(player).getSkillCleanly(skill)

  def getPlayerSkillInfo(player: EntityPlayer, skill: ISkill): SkillInfo = getPlayerSkills(player).getSkillInfo(player, skill)

  def getPlayerSkillInfo(player: EntityPlayer, id: ResourceLocation): SkillInfo = getPlayerSkills(player).getSkillInfo(player, id)

  def sendSkillInfo(player: EntityPlayer, instance: SkillInstance, change: Int): Unit = {
    player match {
      case p: EntityPlayerMP =>
        val info = getPlayerSkillInfo(p, instance.getSkill.getId)
        info.setPercent(instance.getCurrentXp * 1F / instance.getSkill.getLevelRequiredXp(instance.getCurrentLevel))
        info.setName(Utilities.getSkillDesc(instance, change))
        info.activate()
        info.addPlayer(p)
    }
  }

  def clearSkillInfoCache(player: EntityPlayer): Unit = {
    player match {
      case p: EntityPlayerMP => {
        val infos = getPlayerSkills(p).getSkillInfos
        infos.foreach(i => i.removePlayer(p))
        infos.clear()
      }
      case _ =>
    }

  }

  def applySkillAttrs(player: EntityPlayer): Unit = {
    player match {
      case p: EntityPlayerMP => getPlayerSkills(p).getSkills.foreach(i => i.getSkill match {
        case l if (l.shouldCheckOnLogin) => l.checkOnLogin(p, i)
        case _ =>
      })
      case _ =>
    }
  }

  def syncSkills(player: EntityPlayerMP): Unit = {
    if (!player.hasCapability(skillfulCap, null)) return
    val msg = new SkillsSyncMessage
    msg.getTag.setTag("skills", skillfulCap.getStorage.writeNBT(skillfulCap, player.getCapability(skillfulCap, null), null))
    NetworkHandler.instance.sendTo(msg, player)
  }

  def getSkillDesc(skill: SkillInstance, change: Int): ITextComponent = {
    new TextComponentString(
      MessageFormat.format(
        new TextComponentTranslation("info.skillful.skillinfo.format").getFormattedText,
        new TextComponentTranslation(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}").getFormattedText,
        skill.getCurrentLevel.toString,
        skill.getCurrentXp.toString,
        skill.getSkill.getLevelRequiredXp(skill.getCurrentLevel).toString,
        if (change == 0) "" else if (change > 0) s"+${change.toString}" else change.toString
      ))
  }

  def getSkillDescForCmd(skill: SkillInstance): ITextComponent = {
    skill match {
      case s if (s.isClueless) => new TextComponentString(new TextComponentTranslation(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}").getFormattedText
        + " " + new TextComponentTranslation("status.skillful.clueless").setStyle(new Style().setColor(TextFormatting.DARK_GRAY)).getFormattedText).setStyle(new Style().setColor(TextFormatting.DARK_GRAY))
      case s if (s.isCompleted) => new TextComponentString(new TextComponentTranslation(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}").getFormattedText
        + " " + new TextComponentTranslation("status.skillful.max").setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText).setStyle(new Style().setColor(TextFormatting.GREEN))
      case _ => getSkillDesc(skill, 0)
    }
  }

  def getPlayer(server: MinecraftServer, name: String): Option[EntityPlayerMP] = Option(server.getPlayerList.getPlayerByUsername(name))
}
