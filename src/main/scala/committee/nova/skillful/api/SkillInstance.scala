package committee.nova.skillful.api

import committee.nova.skillful.event.impl.{SkillLevelEvent, SkillXpEvent}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.INBTSerializable

import java.util.UUID

class SkillInstance(private var skill: ISkill, val player: UUID) extends INBTSerializable[NBTTagCompound] {
  private var currentXp: Int = 0

  private var currentLevel: Int = 0

  def getSkill: ISkill = skill

  def getCurrentXp: Int = currentXp

  def getCurrentLevel: Int = currentLevel

  def addXp(xp: Int): Unit = {
    val event = SkillXpEvent.Pre(player, skill, xp)
    if (MinecraftForge.EVENT_BUS.post(event)) return
    _addXp(event.getAmount)
  }

  def _addXp(xp: Int): Unit = {
    if (xp < 0) {
      _reduceXp(-xp)
      return
    }
    currentXp += xp
    MinecraftForge.EVENT_BUS.post(SkillXpEvent.Post(player, skill, xp))
    while (currentXp >= skill.getLevelRequiredXp(currentLevel)) {
      currentXp -= skill.getLevelRequiredXp(currentLevel)
      currentLevel += 1
      if (currentLevel > skill.getMaxLevel) cheat()
      else MinecraftForge.EVENT_BUS.post(SkillLevelEvent.Up(player, skill, currentLevel))
    }
  }

  def reduceXp(xp: Int): Unit = {
    val event = SkillXpEvent.Pre(player, skill, -xp)
    if (MinecraftForge.EVENT_BUS.post(event)) return
    _reduceXp(-event.getAmount)
  }

  def _reduceXp(xp: Int): Unit = {
    if (xp < 0) {
      _addXp(-xp)
      return
    }
    currentXp -= xp
    MinecraftForge.EVENT_BUS.post(SkillXpEvent.Post(player, skill, -xp))
    while (currentXp < 0) {
      currentLevel -= 1
      currentXp += skill.getLevelRequiredXp(currentLevel)
      if (currentLevel < 0) clear()
      else MinecraftForge.EVENT_BUS.post(SkillLevelEvent.Down(player, skill, currentLevel))
    }
  }

  def clear(): Unit = {
    currentLevel = 0
    currentXp = 0
  }

  def cheat(): Unit = {
    currentLevel = skill.getMaxLevel
    currentXp = skill.getLevelRequiredXp(currentLevel) - 1
  }

  override def serializeNBT(): NBTTagCompound = {
    val tag = new NBTTagCompound
    tag.setString("skill", skill.getId.toString)
    tag.setInteger("level", currentLevel)
    tag.setInteger("xp", currentXp)
    tag
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    currentLevel = tag.getInteger("level")
    currentXp = tag.getInteger("xp")
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: SkillInstance => that.skill.equals(skill) && that.player.equals(player)
    case _ => false
  }

  override def hashCode(): Int = player.hashCode() + skill.hashCode()
}
