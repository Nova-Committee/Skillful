package committee.nova.skillful.storage

import committee.nova.skillful.Skillful
import committee.nova.skillful.api.ISkill
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.Event

import scala.collection.mutable

object SkillfulStorage {
  class SkillRegisterEvent extends Event {
    def addSkill(skill: ISkill): Unit = SkillfulStorage.addSkill(skill)
  }

  private var skillRegistryFrozen = false
  private val skills: mutable.HashSet[ISkill] = new mutable.HashSet[ISkill]()

  def getSkill(id: ResourceLocation): ISkill = {
    skills.foreach(s => if (s.getId.equals(id)) return s)
    val dummy = new ISkill {
      override def getId: ResourceLocation = id

      override def getMaxLevel: Int = Int.MaxValue

      override def getLevelRequiredXp(level: Int): Int = Int.MaxValue
    }
    skills.add(dummy)
    Skillful.getLogger.warn(s"Skill with id $id not found. Registered a dummy one instead...")
    dummy
  }

  private def addSkill(skill: ISkill): Boolean = {
    val logger = Skillful.getLogger
    val skillName = s"skill with id ${skill.getId.toString}"
    if (skillRegistryFrozen) {
      logger.error(s"Skill registry already frozen! Failed to register $skillName!")
      return false
    }
    val success = skills.add(skill)
    if (success) logger.info(s"Registered $skillName!") else logger.error(s"Failed to register duplicate $skillName!")
    success
  }

  def freeze(): Unit = skillRegistryFrozen = true
}
