package committee.nova.skillful.common.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import committee.nova.skillful.common.manager.SkillTypeManager;
import committee.nova.skillful.common.skill.ISkillType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SkillArgumentType implements ArgumentType<ResourceLocation> {
    private SkillArgumentType() {
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    public static SkillArgumentType skill() {
        return new SkillArgumentType();
    }

    public static ISkillType getSkill(CommandContext<CommandSourceStack> ctx, String id) {
        return SkillTypeManager.getSkillType(ctx.getArgument(id, ResourceLocation.class)).orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof SharedSuggestionProvider ?
                SharedSuggestionProvider.suggest(SkillTypeManager.getSkillTypes()
                        .stream()
                        .map(s -> s.getId().toString())
                        .collect(Collectors.toUnmodifiableSet()), builder) :
                Suggestions.empty();
    }
}
