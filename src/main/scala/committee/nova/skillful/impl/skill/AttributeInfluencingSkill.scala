package committee.nova.skillful.impl.skill

import committee.nova.skillful.api.skill.IApplyAttributeModifiers
import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.ai.attributes.{AttributeModifier, IAttribute}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

import java.util.function.{BiFunction, IntFunction}

class AttributeInfluencingSkill(private val id: ResourceLocation, private val maxLevel: Int, color: BossInfo.Color, fun: Int => Int, attr: IAttribute, modifier: (EntityPlayerMP, SkillInstance) => AttributeModifier) extends Skill(id, maxLevel, color, fun) with IApplyAttributeModifiers {
  def this(id: ResourceLocation, maxLevel: Int, color: BossInfo.Color, attr: IAttribute, modifier: (EntityPlayerMP, SkillInstance) => AttributeModifier) = this(id, maxLevel, color, i => 100 * i, attr, modifier)

  //Java Compatibility
  def this(id: ResourceLocation, maxLevel: Int, color: BossInfo.Color, fun: IntFunction[Integer], attr: IAttribute, modifier: BiFunction[EntityPlayerMP, SkillInstance, AttributeModifier]) =
    this(id, maxLevel, color, i => fun.apply(i), attr, (p, s) => modifier.apply(p, s))

  override def getAttributeModifiers(player: EntityPlayerMP, skillInstance: SkillInstance): Map[IAttribute, AttributeModifier] = Map(attr -> modifier.apply(player, skillInstance))
}
