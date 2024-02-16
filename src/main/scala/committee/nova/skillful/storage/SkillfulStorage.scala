package committee.nova.skillful.storage

import committee.nova.skillful.Skillful
import committee.nova.skillful.api.related.ISkillRelatedFood
import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.{ITextComponent, TextComponentTranslation, TextFormatting}
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

  private var afterInit = false
  private val skills: mutable.LinkedHashSet[ISkill] = new mutable.LinkedHashSet[ISkill]()
  private val foods: mutable.HashSet[ISkillRelatedFood] = new mutable.HashSet[ISkillRelatedFood]()

  def getSkills: Array[ISkill] = skills.toArray

  def getSkill(id: ResourceLocation): ISkill = {
    skills.foreach(s => if (s.getId.equals(id)) return s)
    val dummy = new ISkill {
      private var nameCache: ITextComponent = _

      override def getId: ResourceLocation = id

      override def getMaxLevel: Int = Int.MaxValue

      override def getLevelRequiredXp(level: Int): Int = Int.MaxValue

      override def getColor: TextFormatting = TextFormatting.WHITE

      override def getName: ITextComponent = {
        if (nameCache == null) nameCache = new TextComponentTranslation(
          "skill.skillful.unknown",
          Array(getId.toString)
        )
        nameCache
      }
    }
    skills.add(dummy)
    Skillful.getLogger.warn(s"Skill with id $id not found. Registered a dummy one instead...")
    dummy
  }

  def getSkillStrictly(id: ResourceLocation): Option[ISkill] = {
    skills.foreach(s => if (s.getId.equals(id)) return Some(s))
    None
  }

  def getSkillCleanly(id: ResourceLocation): ISkill = {
    skills.foreach(s => if (s.getId.equals(id)) return s)
    new ISkill {
      private var nameCache: ITextComponent = _

      override def getId: ResourceLocation = id

      override def getMaxLevel: Int = Int.MaxValue

      override def getLevelRequiredXp(level: Int): Int = Int.MaxValue

      override def getColor: TextFormatting = TextFormatting.WHITE

      override def getName: ITextComponent = {
        if (nameCache == null) nameCache = new TextComponentTranslation(
          "skill.skillful.unknown",
          Array(getId.toString)
        )
        nameCache
      }
    }
  }

  def applyFoodEffect(player: EntityPlayerMP, stack: ItemStack): Unit = foods.foreach(s =>
    if (s.getItemFood.equals(stack.getItem)) s.getChange(player, stack).foreach(x => player.getSkillStat(x._1).addXp(player, x._2)))

  private def addSkill(skill: ISkill): Boolean = {
    val logger = Skillful.getLogger
    val skillName = s"skill with id ${skill.getId.toString}"
    if (afterInit) logger.warn(s"$skillName registration is out of preInit lifecycle!")
    val success = skills.add(skill)
    if (success) logger.info(s"Registered $skillName!") else logger.error(s"Failed to register duplicate $skillName!")
    success
  }

  private def addFood(food: ISkillRelatedFood): Unit = foods.add(food)

  def setAfterInit(): Unit = afterInit = true
}
