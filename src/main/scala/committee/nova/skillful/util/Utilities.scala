package committee.nova.skillful.util

import committee.nova.skillful.Skillful
import committee.nova.skillful.api.{ISkill, SkillInstance}
import committee.nova.skillful.player.capabilities.{ISkills, SkillInfo}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation

object Utilities {
  def getPlayerSkills(player: EntityPlayerMP): ISkills = player.getCapability(Skillful.skillfulCap, null)

  def getPlayerSkillStat(player: EntityPlayerMP, skill: ISkill): SkillInstance = getPlayerSkills(player).getSkill(skill)

  def getPlayerSkillStat(player: EntityPlayerMP, id: ResourceLocation): SkillInstance = getPlayerSkills(player).getSkill(id)

  def getPlayerSkillInfo(player: EntityPlayerMP, skill: ISkill): SkillInfo = getPlayerSkills(player).getSkillInfo(skill)

  def getPlayerSkillInfo(player: EntityPlayerMP, id: ResourceLocation): SkillInfo = getPlayerSkills(player).getSkillInfo(id)
}
