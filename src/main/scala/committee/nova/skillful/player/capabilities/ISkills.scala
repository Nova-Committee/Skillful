package committee.nova.skillful.player.capabilities

import committee.nova.skillful.api.{ISkill, SkillInstance}
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.util.ResourceLocation

import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet

trait ISkills {
  def setUUID(uuid: UUID): Unit

  def getUUID: UUID

  def getSkills: CopyOnWriteArraySet[SkillInstance]

  def getSkillInfos: CopyOnWriteArraySet[SkillInfo]

  def getSkillInfo(id: ResourceLocation): SkillInfo

  def getSkillInfo(skill: ISkill): SkillInfo = getSkillInfo(skill.getId)

  def getSkill(skill: ISkill): SkillInstance

  def getSkill(id: ResourceLocation): SkillInstance = getSkill(SkillfulStorage.getSkill(id))
}
