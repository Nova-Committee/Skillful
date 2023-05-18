package committee.nova.skillful.command.arg

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.{Suggestions, SuggestionsBuilder}
import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraft.util.ResourceLocation

import java.util.concurrent.CompletableFuture

object SkillArgument {
  def skill: SkillArgument = new SkillArgument
}

class SkillArgument extends ArgumentType[ISkill] {
  override def listSuggestions[S](context: CommandContext[S], builder: SuggestionsBuilder): CompletableFuture[Suggestions] = {
    SkillfulStorage.getSkills.map(s => s.getId)
      .filter(s => s.toString.startsWith(builder.getRemaining) || s.getPath.startsWith(builder.getRemaining))
      .foreach(s => builder.suggest(s.toString))
    builder.buildFuture()
  }

  override def parse(reader: StringReader): ISkill = SkillfulStorage.getSkillStrictly(new ResourceLocation(reader.readString())).getOrElse(ISkill.EMPTY)
}
