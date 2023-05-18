package committee.nova.skillful.command.impl

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.implicits.Implicits.PlayerEntityImplicit
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.network.message.InfoClearMessage
import committee.nova.skillful.storage.SkillfulStorage
import committee.nova.skillful.util.Utilities
import net.minecraft.command.CommandSource
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.{StringTextComponent, TranslationTextComponent}
import net.minecraftforge.fml.network.PacketDistributor

object SkillfulCommands {
  class ShowSelfArg0 extends Command[CommandSource] {
    override def run(context: CommandContext[CommandSource]): Int = {
      val src = context.getSource
      src.getEntity match {
        case p: ServerPlayerEntity => {
          val skills = SkillfulStorage.getSkills
          if (skills.isEmpty) return 1
          src.sendFeedback(new StringTextComponent(s"${p.getName.getString}:"), false)
          skills.foreach(s => src.sendFeedback(Utilities.getSkillDescForCmd(p.getSkillStat(s)), false))
          1
        }
        case _ => 0
      }
    }
  }

  class ShowSelfArg1 extends Command[CommandSource] {
    override def run(context: CommandContext[CommandSource]): Int = {
      val src = context.getSource
      src.getEntity match {
        case p: ServerPlayerEntity =>
          val skill = context.getArgument("skill_id", classOf[ISkill])
          if (skill.isDummy) {
            src.sendFeedback(new TranslationTextComponent("msg.skillful.skill.notFound"), false)
            return 0
          }
          src.sendFeedback(new StringTextComponent(s"${p.getName.getString}:"), false)
          src.sendFeedback(Utilities.getSkillDescForCmd(p.getSkillStat(skill)), false)
          1
        case _ => 0
      }
    }
  }

  class ShowArg1 extends Command[CommandSource] {
    override def run(context: CommandContext[CommandSource]): Int = {
      val src = context.getSource
      val skills = SkillfulStorage.getSkills
      if (skills.isEmpty) return 1
      val target = EntityArgument.getPlayer(context, "target")
      src.sendFeedback(new StringTextComponent(s"${target.getName.getString}:"), false)
      skills.foreach(s => src.sendFeedback(Utilities.getSkillDescForCmd(target.getSkillStat(s)), false))
      1
    }
  }

  class ShowArg2 extends Command[CommandSource] {
    override def run(context: CommandContext[CommandSource]): Int = {
      val src = context.getSource
      val target = EntityArgument.getPlayer(context, "target")
      val skill = context.getArgument("skill_id", classOf[ISkill])
      if (skill.isDummy) {
        src.sendFeedback(new TranslationTextComponent("msg.skillful.skill.notFound"), false)
        return 0
      }
      src.sendFeedback(new StringTextComponent(s"${target.getName.getString}:"), false)
      src.sendFeedback(Utilities.getSkillDescForCmd(target.getSkillStat(skill)), false)
      1
    }
  }

  class ClearInfo extends Command[CommandSource] {
    override def run(context: CommandContext[CommandSource]): Int = {
      val src = context.getSource
      src.getEntity match {
        case p: ServerPlayerEntity => {
          p.clearSkillInfoCache()
          NetworkHandler.instance.send(PacketDistributor.PLAYER.`with`(() => p), new InfoClearMessage)
          src.sendFeedback(new TranslationTextComponent("msg.skillful.info.cache.clear"), false)
          1
        }
        case _ => 0
      }
    }
  }

  class ChangeXp extends Command[CommandSource] {
    override def run(context: CommandContext[CommandSource]): Int = {
      val src = context.getSource
      val target = EntityArgument.getPlayer(context, "target")
      val skill = context.getArgument("skill_id", classOf[ISkill])
      if (skill.isDummy) {
        src.sendFeedback(new TranslationTextComponent("msg.skillful.skill.notFound"), false)
        return 0
      }
      val v = IntegerArgumentType.getInteger(context, "variation")
      val stat = target.getSkillStat(skill)
      stat.addXp(target, v)
      src.sendFeedback(Utilities.getSkillDescForCmd(stat), false)
      1
    }
  }
}
