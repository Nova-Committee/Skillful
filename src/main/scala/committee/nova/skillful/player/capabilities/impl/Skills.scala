package committee.nova.skillful.player.capabilities.impl

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.event.handler.CapabilityHandler.skillfulCap
import committee.nova.skillful.impl.skill.instance.SkillInstance
import committee.nova.skillful.implicits.Implicits.PlayerEntityImplicit
import committee.nova.skillful.player.capabilities.api.ISkills
import committee.nova.skillful.player.capabilities.info.SkillInfo
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.{CompoundNBT, INBT, ListNBT}
import net.minecraft.util.{Direction, ResourceLocation}
import net.minecraftforge.common.capabilities.Capability.IStorage
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider, ICapabilitySerializable}
import net.minecraftforge.common.util.{LazyOptional, NonNullSupplier}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object Skills {
  class Provider extends ICapabilityProvider with ICapabilitySerializable[CompoundNBT] {
    private val instance = new Impl

    private val storage = new Storage

    override def serializeNBT(): CompoundNBT = {
      val tag = new CompoundNBT
      tag.put("skillful", storage.writeNBT(skillfulCap, instance, null))
      tag
    }

    override def deserializeNBT(nbt: CompoundNBT): Unit = storage.readNBT(skillfulCap, instance, null, nbt.getList("skillful", 10))

    override def getCapability[T](cap: Capability[T], side: Direction): LazyOptional[T] = if (cap == skillfulCap) LazyOptional.of(new NonNullSupplier[ISkills] {
      override def get(): ISkills = instance
    }.asInstanceOf[NonNullSupplier[T]]) else null

  }

  class Storage extends IStorage[ISkills] {
    override def writeNBT(capability: Capability[ISkills], instance: ISkills, side: Direction): INBT = {
      val tag = new ListNBT()
      instance.getSkills.foreach(s => tag.add(s.serializeNBT()))
      tag
    }

    override def readNBT(capability: Capability[ISkills], instance: ISkills, side: Direction, nbt: INBT): Unit = {
      nbt match {
        case tag: ListNBT =>
          val skills = instance.getSkills
          skills.clear()
          tag.asScala.foreach {
            case t: CompoundNBT =>
              val skill = SkillfulStorage.getSkill(new ResourceLocation(t.getString("skill")))
              val i = new SkillInstance(skill)
              i.deserializeNBT(t)
              skills.add(i)
            case _ =>
          }
        case _ =>
      }
    }
  }

  class Impl extends ISkills {
    private val skills: mutable.HashSet[SkillInstance] = new mutable.HashSet[SkillInstance]()

    private val skillInfos: mutable.HashSet[SkillInfo] = new mutable.HashSet[SkillInfo]()

    override def getSkills: mutable.HashSet[SkillInstance] = skills

    override def getSkill(skill: ISkill): SkillInstance = {
      skills.foreach(s => if (s.getSkill.equals(skill)) return s)
      val instance = new SkillInstance(skill)
      skills.add(instance)
      instance
    }

    override def getSkillCleanly(skill: ISkill): Option[SkillInstance] = {
      skills.foreach(s => if (s.getSkill.equals(skill)) return Some(s))
      None
    }

    override def getSkillInfos: mutable.HashSet[SkillInfo] = skillInfos

    override def getSkillInfo(player: PlayerEntity, skill: ISkill): SkillInfo = {
      skillInfos.foreach(i => if (i.getSkillInstance.getSkill.equals(skill)) return i)
      val instance = new SkillInfo(player.getSkillStat(skill))
      skillInfos.add(instance)
      instance
    }

    override def removeSkill(id: ResourceLocation): Boolean = {
      skills.foreach(s => if (id.equals(s.getSkill.getId)) return skills.remove(s))
      false
    }
  }
}
