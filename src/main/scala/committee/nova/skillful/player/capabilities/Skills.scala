package committee.nova.skillful.player.capabilities

import committee.nova.skillful.Skillful.skillfulCap
import committee.nova.skillful.`implicit`.Implicits.EntityPlayerMPImplicit
import committee.nova.skillful.api.ISkill
import committee.nova.skillful.skills.SkillInstance
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.{NBTBase, NBTTagCompound, NBTTagList}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.Capability.IStorage
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import scala.collection.JavaConverters._
import scala.collection.mutable

object Skills {
  class Provider extends ICapabilitySerializable[NBTTagCompound] {
    private val instance = new Impl

    private val storage = new Storage

    override def serializeNBT(): NBTTagCompound = {
      val tag = new NBTTagCompound
      tag.setTag("skillful", storage.writeNBT(skillfulCap, instance, null))
      tag
    }

    override def deserializeNBT(nbt: NBTTagCompound): Unit = storage.readNBT(skillfulCap, instance, null, nbt.getTagList("skillful", 10))

    override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = capability == skillfulCap

    override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = if (capability == skillfulCap) skillfulCap.cast(instance) else null.asInstanceOf[T]
  }

  class Storage extends IStorage[ISkills] {
    override def writeNBT(capability: Capability[ISkills], instance: ISkills, side: EnumFacing): NBTBase = {
      val tag = new NBTTagList
      instance.getSkills.foreach(s => tag.appendTag(s.serializeNBT()))
      tag
    }

    override def readNBT(capability: Capability[ISkills], instance: ISkills, side: EnumFacing, nbt: NBTBase): Unit = {
      nbt match {
        case tag: NBTTagList =>
          val skills = instance.getSkills
          skills.clear()
          tag.asScala.foreach {
            case t: NBTTagCompound =>
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

    override def getSkillInfos: mutable.HashSet[SkillInfo] = skillInfos

    override def getSkillInfo(player: EntityPlayerMP, skill: ISkill): SkillInfo = {
      skillInfos.foreach(i => if (i.getSkillInstance.getSkill.equals(skill)) return i)
      val instance = new SkillInfo(player.getSkillStat(skill))
      skillInfos.add(instance)
      instance
    }
  }
}
