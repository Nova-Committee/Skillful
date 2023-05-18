package committee.nova.skillful.impl.skill

import committee.nova.skillful.api.skill.IApplyAttributeModifiers
import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.ai.attributes.{Attribute, AttributeModifier}
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

@Deprecated
class AttributeInfluencingSkill(private val id: ResourceLocation, private val maxLevel: Int, color: BossInfo.Color, fun: Int => Int, attr: Attribute, modifier: (ServerPlayerEntity, SkillInstance) => AttributeModifier) extends Skill(id, maxLevel, color, fun) with IApplyAttributeModifiers {
  def this(id: ResourceLocation, maxLevel: Int, color: BossInfo.Color, attr: Attribute, modifier: (ServerPlayerEntity, SkillInstance) => AttributeModifier) = this(id, maxLevel, color, i => 100 * i, attr, modifier)

  //Java Compatibility

  override def getAttributeModifiers(player: ServerPlayerEntity, skillInstance: SkillInstance): Map[Attribute, AttributeModifier] = Map(attr -> modifier.apply(player, skillInstance))
}
