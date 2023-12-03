package committee.nova.skillful.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import committee.nova.skillful.Skillful;
import committee.nova.skillful.common.cap.skill.Skills;
import committee.nova.skillful.common.command.arg.SkillArgumentType;
import committee.nova.skillful.common.skill.ISkillType;
import committee.nova.skillful.common.skill.SkillInstance;
import committee.nova.skillful.common.util.Utilities;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.function.Predicate;

public class CommandManager {
    private static final Predicate<CommandSourceStack> isPlayer = s -> {
        try {
            s.getPlayerOrException();
            return true;
        } catch (CommandSyntaxException ignored) {

        }
        return false;
    };
    private static final Predicate<CommandSourceStack> isOp = isPlayer
            .and(s -> s.hasPermission(s.getServer().getOperatorUserPermissionLevel()));

    private static int list(CommandContext<CommandSourceStack> ctx, ServerPlayer target) throws CommandSyntaxException {
        final ServerPlayer player = ctx.getSource().getPlayerOrException();
        Utilities.getSkillList4Cmd(target).forEach(t -> player.displayClientMessage(t, false));
        return 1;
    }

    private static int query(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        final ISkillType skill = SkillArgumentType.getSkill(ctx, "skill_id");
        if (skill == null) {
            ctx.getSource().sendFailure(new TranslatableComponent("cmd.skillful.skill.not_found"));
            return 0;
        }
        target.getCapability(Skills.SKILLS_CAPABILITY).ifPresent(i -> {
            final Optional<SkillInstance> instance = i.getSkill(skill);
            ctx.getSource().sendSuccess(new TranslatableComponent("cmd.skillful.skill.list", target.getName())
                    .withStyle(ChatFormatting.YELLOW), false);
            ctx.getSource().sendSuccess(instance
                    .map(skillInstance -> Utilities.getSkillInfo4Cmd(target, skillInstance))
                            .orElseGet(() -> new TranslatableComponent("cmd.skillful.skill.clueless", skill.getName())),
                    false);
        });
        return 1;
    }

    private static int changeXp(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        final ISkillType skill = SkillArgumentType.getSkill(ctx, "skill_id");
        if (skill == null) {
            ctx.getSource().sendFailure(new TranslatableComponent("cmd.skillful.skill.not_found"));
            return 0;
        }
        target.getCapability(Skills.SKILLS_CAPABILITY).ifPresent(i -> {
            final SkillInstance instance = i.getOrCreateSkill(skill);
            instance.changeXp(target, LongArgumentType.getLong(ctx, "change"));
            ctx.getSource().sendSuccess(new TranslatableComponent("cmd.skillful.skill.list", target.getName())
                    .withStyle(ChatFormatting.YELLOW), false);
            ctx.getSource().sendSuccess(Utilities.getSkillInfo4Cmd(target, instance), false);
        });
        return 1;
    }

    private static int changeLevel(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        final ISkillType skill = SkillArgumentType.getSkill(ctx, "skill_id");
        if (skill == null) {
            ctx.getSource().sendFailure(new TranslatableComponent("cmd.skillful.skill.not_found"));
            return 0;
        }
        target.getCapability(Skills.SKILLS_CAPABILITY).ifPresent(i -> {
            final SkillInstance instance = i.getOrCreateSkill(skill);
            instance.changeToLevel(target, LongArgumentType.getLong(ctx, "change"));
            ctx.getSource().sendSuccess(new TranslatableComponent("cmd.skillful.skill.list", target.getName())
                    .withStyle(ChatFormatting.YELLOW), false);
            ctx.getSource().sendSuccess(Utilities.getSkillInfo4Cmd(target, instance), false);
        });
        return 1;
    }

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        ArgumentTypes.register("skillful_skill", SkillArgumentType.class, new EmptyArgumentSerializer<>(SkillArgumentType::skill));
        final var rootCmd = Commands.literal(Skillful.MODID)
                .requires(s -> true)
                .then(Commands.literal("list")
                        .requires(isPlayer)
                        .executes(ctx -> list(ctx, ctx.getSource().getPlayerOrException())))
                .then(Commands.literal("skill")
                        .requires(isPlayer)
                        .then(Commands.argument("skill_id", SkillArgumentType.skill())
                                .requires(isPlayer)
                                .executes(ctx -> query(ctx, ctx.getSource().getPlayerOrException()))
                                .then(Commands.literal("query")
                                        .requires(isPlayer)
                                        .executes(ctx -> query(ctx, ctx.getSource().getPlayerOrException())))
                                .then(Commands.literal("changexp")
                                        .requires(isOp)
                                        .then(Commands.argument("change", LongArgumentType.longArg())
                                                .requires(isOp)
                                                .executes(ctx -> changeXp(ctx, ctx.getSource().getPlayerOrException()))))
                                .then(Commands.literal("changelevel")
                                        .requires(isOp)
                                        .then(Commands.argument("change", LongArgumentType.longArg())
                                                .requires(isOp)
                                                .executes(ctx -> changeLevel(ctx, ctx.getSource().getPlayerOrException()))))))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(isOp)
                        .then(Commands.literal("skill")
                                .requires(isOp)
                                .then(Commands.argument("skill_id", SkillArgumentType.skill())
                                        .requires(isOp)
                                        .executes(ctx -> query(ctx, EntityArgument.getPlayer(ctx, "player")))
                                        .then(Commands.literal("query")
                                                .requires(isOp)
                                                .executes(ctx -> query(ctx, EntityArgument.getPlayer(ctx, "player"))))
                                        .then(Commands.literal("changexp")
                                                .requires(isOp)
                                                .then(Commands.argument("change", LongArgumentType.longArg())
                                                        .requires(isOp)
                                                        .executes(ctx -> changeXp(ctx, EntityArgument.getPlayer(ctx, "player")))))
                                        .then(Commands.literal("changelevel")
                                                .requires(isOp)
                                                .then(Commands.argument("change", LongArgumentType.longArg())
                                                        .requires(isOp)
                                                        .executes(ctx -> changeLevel(ctx, EntityArgument.getPlayer(ctx, "player"))))))));
        dispatcher.register(rootCmd);
    }
}
