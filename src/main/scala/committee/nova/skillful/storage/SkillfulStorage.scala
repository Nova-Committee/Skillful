package committee.nova.skillful.storage

import committee.nova.skillful.Skillful
import committee.nova.skillful.api.related.ISkillRelatedFood
import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo
import net.minecraftforge.fml.common.eventhandler.Event

import scala.collection.mutable

object SkillfulStorage {
  class SkillRegisterEvent extends Event {
    def addSkill(skill: ISkill): Unit = SkillfulStorage.addSkill(skill)

    def addSkills(skills: ISkill*): Unit = for (skill <- skills) addSkill(skill)
  }

  class SkillRelatedFoodRegisterEvent extends Event {
    def addSkillRelatedFood(food: ISkillRelatedFood): Unit = addFood(food)

    def addSkillRelatedFoods(foods: ISkillRelatedFood*): Unit = for (food <- foods) addFood(food)
  }

  private var skillRegistryFrozen = false
  private val skills: mutable.HashSet[ISkill] = new mutable.HashSet[ISkill]()
  private val foods: mutable.HashSet[ISkillRelatedFood] = new mutable.HashSet[ISkillRelatedFood]()

  def getSkills: Array[ISkill] = skills.toArray

  def getSkill(id: ResourceLocation): ISkill = {
    skills.foreach(s => if (s.getId.equals(id)) return s)
    val dummy = new ISkill {
      override def getId: ResourceLocation = id

      override def getMaxLevel: Int = Int.MaxValue

      override def getLevelRequiredXp(level: Int): Int = Int.MaxValue

      override def getColor: BossInfo.Color = BossInfo.Color.WHITE
    }
    skills.add(dummy)
    Skillful.getLogger.warn(s"Skill with id $id not found. Registered a dummy one instead...")
    dummy
  }

  def getSkillStrictly(id: ResourceLocation): Option[ISkill] = {
    skills.foreach(s => if (s.getId.equals(id)) return Some(s))
    None
  }

  def applyFoodEffect(player: EntityPlayerMP, stack: ItemStack): Unit = foods.foreach(s =>
    if (s.getItemFood.equals(stack.getItem)) s.getChange(player, stack).foreach(
      x => player.getSkillStat(x._1).addXp(player, x._2)))

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

  private def addFood(food: ISkillRelatedFood): Unit = foods.add(food)

  def freezeSkillReg(): Unit = skillRegistryFrozen = true
}
