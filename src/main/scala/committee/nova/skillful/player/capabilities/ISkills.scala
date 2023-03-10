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

  def getSkill(skill: ISkill): SkillInstance

  def getSkill(id: ResourceLocation): SkillInstance = getSkill(SkillfulStorage.getSkill(id))
}
