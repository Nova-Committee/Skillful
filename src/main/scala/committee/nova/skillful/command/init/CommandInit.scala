package committee.nova.skillful.command.init

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import committee.nova.skillful.command.arg.SkillArgument
import committee.nova.skillful.command.impl.SkillfulCommands._
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.{CommandSource, Commands}

object CommandInit {
  def init(dispatcher: CommandDispatcher[CommandSource]): Unit = {
    dispatcher.register(
      Commands.literal("skillful")
        .`then`(
          Commands.literal("showself")
            .`then`(
              Commands.argument("skill_id", SkillArgument.skill)
                .requires(_ => true)
                .executes(new ShowSelfArg1)
            )
            .requires(_ => true)
            .executes(new ShowSelfArg0)
        )
        .`then`(
          Commands.literal("show")
            .`then`(
              Commands.argument("target", EntityArgument.player())
                .`then`(
                  Commands.argument("skill_id", SkillArgument.skill)
                    .requires(s => s.hasPermissionLevel(2))
                    .executes(new ShowArg2)
                )
                .requires(s => s.hasPermissionLevel(2))
                .executes(new ShowArg1)
            )
            .requires(s => s.hasPermissionLevel(2))
        )
        .`then`(
          Commands.literal("clearinfo")
            .requires(_ => true)
            .executes(new ClearInfo)
        )
        .`then`(
          Commands.literal("changexp")
            .`then`(
              Commands.argument("target", EntityArgument.player())
                .`then`(
                  Commands.argument("skill_id", SkillArgument.skill)
                    .`then`(
                      Commands.argument("variation", IntegerArgumentType.integer(1))
                        .requires(s => s.hasPermissionLevel(s.getServer.getOpPermissionLevel))
                        .executes(new ChangeXp)
                    )
                    .requires(s => s.hasPermissionLevel(s.getServer.getOpPermissionLevel))
                )
                .requires(s => s.hasPermissionLevel(s.getServer.getOpPermissionLevel))
            )
            .requires(s => s.hasPermissionLevel(s.getServer.getOpPermissionLevel))
        )
    )
  }
}
