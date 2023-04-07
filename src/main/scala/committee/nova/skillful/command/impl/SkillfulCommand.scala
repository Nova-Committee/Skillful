package committee.nova.skillful.command.impl

import com.google.common.collect.ImmutableList
import committee.nova.skillful.command.impl.SkillfulCommand.{ChangeXPCommand, ClearInfoCommand, ShowCommand, ShowSelfCommand}
import committee.nova.skillful.implicits.Implicits.EntityPlayerImplicit
import committee.nova.skillful.network.handler.NetworkHandler
import committee.nova.skillful.network.message.InfoClearMessage
import committee.nova.skillful.storage.SkillfulStorage
import committee.nova.skillful.util.Utilities
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.{Style, TextComponentString, TextComponentTranslation, TextFormatting}
import net.minecraftforge.server.command.CommandTreeBase

import java.util
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.util.Try

object SkillfulCommand {
  class ShowSelfCommand extends CommandBase {
    override def getName: String = "showself"

    override def getUsage(sender: ICommandSender): String = "/skillful showself [SkillId]"

    override def checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean = true

    override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
      if (!sender.isInstanceOf[EntityPlayerMP]) return
      val player = sender.asInstanceOf[EntityPlayerMP]
      args.length match {
        case 0 =>
          player.sendMessage(new TextComponentString(s"${player.getName}:"))
          SkillfulStorage.getSkills.foreach(s => player.sendMessage(Utilities.getSkillDescForCmd(player.getSkillStat(s))))
        case 1 =>
          SkillfulStorage.getSkillStrictly(Try(new ResourceLocation(args(0))).getOrElse(new ResourceLocation("invalidInvalid"))) match {
            case Some(x) => {
              player.sendMessage(Utilities.getSkillDescForCmd(player.getSkillStat(x)))
              player.sendSkillInfo(player.getSkillStat(x), 0)
            }
            case None => player.sendMessage(new TextComponentTranslation("msg.skillful.skill.notFound")
              .setStyle(new Style().setColor(TextFormatting.DARK_RED)))
          }
        case _ => player.sendMessage(new TextComponentString(getUsage(player)))
      }
    }

    override def getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array[String], targetPos: BlockPos): util.List[String] = {
      if (args.length != 1) ImmutableList.of() else CommandBase.getListOfStringsMatchingLastWord(args, SkillfulStorage.getSkills.map(s => s.getId.toString).toArray: _*)
    }
  }

  class ShowCommand extends CommandBase {
    override def getName: String = "show"

    override def getUsage(sender: ICommandSender): String = "/skillful show [Player] [SkillId]"

    override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
      if (args.length == 0) {
        sender.sendMessage(new TextComponentString(getUsage(sender)))
        return
      }
      Utilities.getPlayer(server, args(0)) match {
        case Some(x) =>
          if (x.isFake) return
          args.length match {
            case 1 => sender.sendMessage(new TextComponentString(s"${x.getName}:"))
              SkillfulStorage.getSkills.foreach(s => sender.sendMessage(Utilities.getSkillDescForCmd(x.getSkillStat(s))))
            case 2 => SkillfulStorage.getSkillStrictly(Try(new ResourceLocation(args(1))).getOrElse(new ResourceLocation("invalidInvalid"))) match {
              case Some(z) => sender.sendMessage(Utilities.getSkillDescForCmd(x.getSkillStat(z)))
              case None => sender.sendMessage(new TextComponentTranslation("msg.skillful.skill.notFound")
                .setStyle(new Style().setColor(TextFormatting.DARK_RED)))
            }
            case _ => sender.sendMessage(new TextComponentString(getUsage(sender)))
          }
        case None => sender.sendMessage(new TextComponentTranslation("msg.skillful.player.notFound"))
      }
    }

    override def getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array[String], targetPos: BlockPos): util.List[String] = {
      CommandBase.getListOfStringsMatchingLastWord(args, (args.length match {
        case 1 => server.getPlayerList.getOnlinePlayerNames
        case 2 => SkillfulStorage.getSkills.map(s => s.getId.toString).toArray
        case _ => Array("")
      }): _*)
    }
  }

  class ClearInfoCommand extends CommandBase {
    override def getName: String = "clearinfo"

    override def getUsage(sender: ICommandSender): String = "/skillful clearinfo"

    override def checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean = true

    override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
      sender match {
        case p: EntityPlayerMP => {
          p.clearSkillInfoCache()
          NetworkHandler.instance.sendTo(new InfoClearMessage, p)
          p.sendMessage(new TextComponentTranslation("msg.skillful.info.cache.clear")
            .setStyle(new Style().setColor(TextFormatting.GREEN)))
        }
        case _ =>
      }
    }
  }

  class ChangeXPCommand extends CommandBase {
    override def getName: String = "changexp"

    override def getUsage(sender: ICommandSender): String = "/skillful changexp [Player] [SkillId] [Variation]"

    override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
      if (args.length != 3) {
        sender.sendMessage(new TextComponentString(getUsage(sender)))
        return
      }
      val target = server.getPlayerList.getPlayerByUsername(args(0))
      if (target == null) {
        sender.sendMessage(new TextComponentTranslation("msg.skillful.player.notFound"))
        return
      }
      SkillfulStorage.getSkillStrictly(Try(new ResourceLocation(args(1))).getOrElse(new ResourceLocation("invalidInvalid"))) match {
        case Some(s) => {
          Try(Integer.parseInt(args(2))).foreach(i => {
            val instance = target.getSkillStat(s)
            instance.addXp(target, i)
            sender.sendMessage(new TextComponentTranslation("msg.skillful.change.applied")
              .setStyle(new Style().setColor(TextFormatting.GREEN)))
            sender.sendMessage(Utilities.getSkillDescForCmd(instance))
            return
          })
        }
        case None => sender.sendMessage(new TextComponentTranslation("msg.skillful.skill.notFound"))
      }
    }

    override def getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array[String], targetPos: BlockPos): util.List[String] = {
      CommandBase.getListOfStringsMatchingLastWord(args, (args.length match {
        case 1 => server.getPlayerList.getOnlinePlayerNames
        case 2 => SkillfulStorage.getSkills.map(s => s.getId.toString).toArray
        case _ => Array("")
      }): _*)
    }
  }
}

class SkillfulCommand extends CommandTreeBase {
  addSubcommand(new ShowSelfCommand)
  addSubcommand(new ShowCommand)
  addSubcommand(new ClearInfoCommand)
  addSubcommand(new ChangeXPCommand)

  override def getName: String = "skillful"

  override def checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean = true

  override def getUsage(sender: ICommandSender): String = {
    val buffer = new StringBuffer()
    for (c <- this.getSubCommands.asScala) buffer.append(c.getUsage(sender) + ";")
    buffer.toString
  }
}
