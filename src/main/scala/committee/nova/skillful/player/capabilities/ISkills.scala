package committee.nova.skillful.player.capabilities

import committee.nova.skillful.api.ISkill
import committee.nova.skillful.skills.SkillInstance
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

trait ISkills {
  def getSkills: mutable.HashSet[SkillInstance]

  def getSkillInfos: mutable.HashSet[SkillInfo]

  def getSkillInfo(player: EntityPlayerMP, id: ResourceLocation): SkillInfo = getSkillInfo(player, SkillfulStorage.getSkill(id))

  def getSkillInfo(player: EntityPlayerMP, skill: ISkill): SkillInfo

  def getSkill(skill: ISkill): SkillInstance

  def getSkill(id: ResourceLocation): SkillInstance = getSkill(SkillfulStorage.getSkill(id))
}
