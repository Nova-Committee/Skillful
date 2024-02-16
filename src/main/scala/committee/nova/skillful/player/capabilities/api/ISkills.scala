package committee.nova.skillful.player.capabilities.api

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

trait ISkills {
  def getSkills: mutable.HashSet[SkillInstance]

  def getSkill(skill: ISkill): SkillInstance

  def getSkill(id: ResourceLocation): SkillInstance = getSkill(SkillfulStorage.getSkill(id))

  def getSkillStrictly(id: ResourceLocation): Option[SkillInstance] = SkillfulStorage.getSkillStrictly(id) match {
    case Some(s) => Some(getSkill(s))
    case None => None
  }

  def getSkillCleanly(skill: ISkill): Option[SkillInstance]

  def getSkillCleanly(id: ResourceLocation): Option[SkillInstance] = getSkillCleanly(SkillfulStorage.getSkillCleanly(id))

  def removeSkill(id: ResourceLocation): Boolean

  def removeSkill(skill: ISkill): Boolean = removeSkill(skill.getId)

}
