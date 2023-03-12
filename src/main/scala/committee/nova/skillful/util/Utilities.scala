package committee.nova.skillful.util

import committee.nova.skillful.Skillful
import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.api.ISkill
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.network.message.SkillsSyncMessage
import committee.nova.skillful.player.capabilities.{ISkills, SkillInfo}
import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.util.ResourceLocation

object Utilities {
  def getPlayerSkills(player: EntityPlayer): ISkills = player.getCapability(Skillful.skillfulCap, null)

  def getPlayerSkillStat(player: EntityPlayer, skill: ISkill): SkillInstance = getPlayerSkills(player).getSkill(skill)

  def getPlayerSkillStat(player: EntityPlayer, id: ResourceLocation): SkillInstance = getPlayerSkills(player).getSkill(id)

  def getPlayerSkillInfo(player: EntityPlayer, skill: ISkill): SkillInfo = getPlayerSkills(player).getSkillInfo(player, skill)

  def getPlayerSkillInfo(player: EntityPlayer, id: ResourceLocation): SkillInfo = getPlayerSkills(player).getSkillInfo(player, id)

  def sendSkillInfo(player: EntityPlayer, instance: SkillInstance): Unit = {
    player match {
      case p: EntityPlayerMP =>
        val info = getPlayerSkillInfo(p, instance.getSkill.getId)
        info.setPercent(instance.getCurrentXp * 1F / instance.getSkill.getLevelRequiredXp(instance.getCurrentLevel))
        info.activate()
        info.addPlayer(p)
    }
  }

  def syncSkills(player: EntityPlayerMP): Unit = {
    if (!player.hasCapability(skillfulCap, null)) return
    val msg = new SkillsSyncMessage
    msg.getTag.setTag("skills", skillfulCap.getStorage.writeNBT(skillfulCap, player.getCapability(skillfulCap, null), null))
    NetworkHandler.instance.sendTo(msg, player)
  }
}
