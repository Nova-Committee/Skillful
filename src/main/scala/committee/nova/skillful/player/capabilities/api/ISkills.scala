package committee.nova.skillful.player.capabilities.api

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.manager.SkillfulManager
import committee.nova.skillful.player.capabilities.info.SkillInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

trait ISkills {
  def getSkills: mutable.HashSet[SkillInstance]

  def getSkillInfos: mutable.HashSet[SkillInfo]

  def getSkillInfo(player: PlayerEntity, id: ResourceLocation): SkillInfo = getSkillInfo(player, SkillfulManager.getSkill(id))

  def getSkillInfo(player: PlayerEntity, skill: ISkill): SkillInfo

  def getSkill(skill: ISkill): SkillInstance

  def getSkill(id: ResourceLocation): SkillInstance = getSkill(SkillfulManager.getSkill(id))

  def getSkillStrictly(id: ResourceLocation): Option[SkillInstance] = SkillfulManager.getSkillStrictly(id) match {
    case Some(s) => Some(getSkill(s))
    case None => None
  }

  def getSkillCleanly(skill: ISkill): Option[SkillInstance]

  def getSkillCleanly(id: ResourceLocation): Option[SkillInstance] = getSkillCleanly(SkillfulManager.getSkillCleanly(id))

  def removeSkill(id: ResourceLocation): Boolean

  def removeSkill(skill: ISkill): Boolean = removeSkill(skill.getId)

}
