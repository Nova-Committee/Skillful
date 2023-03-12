package committee.nova.skillful.skills

import committee.nova.skillful.api.IApplyAttributeModifiers
import net.minecraft.entity.ai.attributes.{AttributeModifier, IAttribute}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.world.BossInfo

class AttributeInfluencingSkill(private val id: ResourceLocation, private val maxLevel: Int, color: BossInfo.Color, attr: IAttribute, modifier: (EntityPlayerMP, SkillInstance) => AttributeModifier) extends Skill(id, maxLevel, color) with IApplyAttributeModifiers {
  override def getAttributeModifiers(player: EntityPlayerMP, skillInstance: SkillInstance): Map[IAttribute, AttributeModifier] = Map(attr -> modifier.apply(player, skillInstance))
}
