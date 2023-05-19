package committee.nova.skillful.manager

import committee.nova.skillful.Constants
import committee.nova.skillful.api.related.ISkillRelatedFood
import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.implicits.Implicits.PlayerEntityImplicit
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

import scala.collection.mutable

object SkillfulManager {
  private var afterInit = false
  private val skills: mutable.LinkedHashSet[ISkill] = new mutable.LinkedHashSet[ISkill]()
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
    Constants.LOGGER.warn(s"Skill with id $id not found. Registered a dummy one instead...")
    dummy
  }

  def getSkillStrictly(id: ResourceLocation): Option[ISkill] = {
    skills.foreach(s => if (s.getId.equals(id)) return Some(s))
    None
  }

  def getSkillCleanly(id: ResourceLocation): ISkill = {
    skills.foreach(s => if (s.getId.equals(id)) return s)
    new ISkill {
      override def getId: ResourceLocation = id

      override def getMaxLevel: Int = Int.MaxValue

      override def getLevelRequiredXp(level: Int): Int = Int.MaxValue

      override def getColor: BossInfo.Color = BossInfo.Color.WHITE
    }
  }

  def applyFoodEffect(player: ServerPlayerEntity, stack: ItemStack): Unit = foods.foreach(s =>
    if (s.getItemFood.equals(stack.getItem)) s.getChange(player, stack).foreach(x => player.getSkillStat(x._1).addXp(player, x._2)))

  def addSkill(skill: ISkill): Boolean = {
    val logger = Constants.LOGGER
    val skillName = s"skill with id ${skill.getId.toString}"
    if (afterInit) {
      logger.error(s"$skillName registration is after initialization, cancelling...")
      return false
    }
    val success = skills.add(skill)
    if (success) logger.info(s"Registered $skillName!") else logger.error(s"Failed to register duplicate $skillName!")
    success
  }

  def addFood(food: ISkillRelatedFood): Boolean = {
    val logger = Constants.LOGGER
    val foodName = s"skill-related food ${food.getItemFood.getRegistryName.toString}"
    if (afterInit) {
      logger.error(s"$foodName registration is out of preInit lifecycle, cancelling...")
      return false
    }
    val success = foods.add(food)
    if (success) logger.info(s"Registered skill-related food $foodName!")
    else logger.error(s"Failed to register duplicate skill-related food $foodName!")
    success
  }

  def setAfterInit(): Unit = afterInit = true
}
