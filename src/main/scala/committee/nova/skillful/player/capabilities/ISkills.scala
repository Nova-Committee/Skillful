package committee.nova.skillful.player.capabilities

import committee.nova.skillful.api.ISkill
import committee.nova.skillful.skills.SkillInstance
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.util.ResourceLocation

import java.util.UUID
import scala.collection.mutable

trait ISkills {
  def setUUID(uuid: UUID): Unit

  def getUUID: UUID

  def getSkills: mutable.HashSet[SkillInstance]

  def getSkillInfos: mutable.HashSet[SkillInfo]

  def getSkillInfo(id: ResourceLocation): SkillInfo

  def getSkillInfo(skill: ISkill): SkillInfo = getSkillInfo(skill.getId)

  def getSkill(skill: ISkill): SkillInstance

  def getSkill(id: ResourceLocation): SkillInstance = getSkill(SkillfulStorage.getSkill(id))
}
