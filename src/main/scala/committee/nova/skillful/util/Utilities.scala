package committee.nova.skillful.util

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.event.handler.CapabilityHandler.skillfulCap
import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.network.message.SkillsSyncMessage
import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.info.SkillInfo
import net.minecraft.entity.player.{PlayerEntity, ServerPlayerEntity}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text._
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.network.PacketDistributor

object Utilities {
  def getPlayerSkills(player: PlayerEntity): ISkills = player.getCapability(skillfulCap, null).orElse(null)

  def getPlayerSkillStat(player: PlayerEntity, skill: ISkill): SkillInstance = getPlayerSkills(player).getSkill(skill)

  def getPlayerSkillStat(player: PlayerEntity, id: ResourceLocation): SkillInstance = getPlayerSkills(player).getSkill(id)

  def getPlayerSkillStatStrictly(player: PlayerEntity, id: ResourceLocation): Option[SkillInstance] = getPlayerSkills(player).getSkillStrictly(id)

  def getPlayerSkillStatCleanly(player: PlayerEntity, id: ResourceLocation): Option[SkillInstance] = getPlayerSkills(player).getSkillCleanly(id)

  def getPlayerSkillStatCleanly(player: PlayerEntity, skill: ISkill): Option[SkillInstance] = getPlayerSkills(player).getSkillCleanly(skill)

  def removePlayerSkill(player: PlayerEntity, id: ResourceLocation): Boolean = getPlayerSkills(player).removeSkill(id)

  def removePlayerSkill(player: PlayerEntity, skill: ISkill): Boolean = getPlayerSkills(player).removeSkill(skill)

  def getPlayerSkillInfo(player: PlayerEntity, skill: ISkill): SkillInfo = getPlayerSkills(player).getSkillInfo(player, skill)

  def getPlayerSkillInfo(player: PlayerEntity, id: ResourceLocation): SkillInfo = getPlayerSkills(player).getSkillInfo(player, id)

  def sendSkillInfo(player: PlayerEntity, instance: SkillInstance, change: Int): Unit = {
    player match {
      case _: FakePlayer =>
      case p: ServerPlayerEntity =>
        val info = getPlayerSkillInfo(p, instance.getSkill.getId)
        info.setPercent(instance.getCurrentXp * 1F / instance.getSkill.getLevelRequiredXp(instance.getCurrentLevel))
        info.setName(Utilities.getSkillDesc(instance, change))
        info.activate()
        info.addPlayer(p)
    }
  }

  def clearSkillInfoCache(player: PlayerEntity): Unit = {
    player match {
      case _: FakePlayer =>
      case p: ServerPlayerEntity =>
        val infos = getPlayerSkills(p).getSkillInfos
        infos.foreach(i => i.removePlayer(p))
        infos.clear()
      case _ =>
    }

  }

  def applySkillAttrs(player: PlayerEntity): Unit = {
    player match {
      case _: FakePlayer =>
      case p: ServerPlayerEntity => getPlayerSkills(p).getSkills.foreach(i => i.getSkill match {
        case l if l.shouldCheckOnLogin => l.checkOnLogin(p, i)
        case _ =>
      })
      case _ =>
    }
  }

  def syncSkills(player: ServerPlayerEntity): Unit = {
    if (isPlayerFake(player)) return
    player.getCapability(skillfulCap).ifPresent(s => {
      val msg = new SkillsSyncMessage
      msg.getTag.put("skills", skillfulCap.getStorage.writeNBT(skillfulCap, s, null))
      NetworkHandler.instance.send(PacketDistributor.PLAYER.`with`(() => player), msg)
    })
  }

  def getSkillDesc(skill: SkillInstance, change: Int): ITextComponent = {
    new TranslationTextComponent("info.skillful.skillinfo.format", Array(new TranslationTextComponent(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}"),
      skill.getCurrentLevel.toString,
      skill.getCurrentXp.toString,
      skill.getSkill.getLevelRequiredXp(skill.getCurrentLevel).toString,
      if (change == 0) "" else if (change > 0) " << " + s"+${change.toString}" else change.toString): _*)
  }

  def getSkillDescForCmd(skill: SkillInstance): ITextComponent = {
    skill match {
      case s if s.isClueless => new TranslationTextComponent("info.skillful.skillinfo.cmd.format",
        new TranslationTextComponent(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}"),
        new TranslationTextComponent("status.skillful.clueless")
      ).mergeStyle(TextFormatting.DARK_GRAY)
      case s if s.isCompleted => new TranslationTextComponent("info.skillful.skillinfo.cmd.format",
        new TranslationTextComponent(s"skill.${skill.getSkill.getId.getNamespace}.${skill.getSkill.getId.getPath}"),
        new TranslationTextComponent("status.skillful.max")
      ).mergeStyle(TextFormatting.GREEN)
      case _ => getSkillDesc(skill, 0)
    }
  }

  def getPlayer(server: MinecraftServer, name: String): Option[ServerPlayerEntity] = Option(server.getPlayerList.getPlayerByUsername(name))

  def isPlayerFake(player: PlayerEntity): Boolean = player.isInstanceOf[FakePlayer]
}
