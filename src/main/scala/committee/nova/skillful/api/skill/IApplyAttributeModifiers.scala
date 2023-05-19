package committee.nova.skillful.api.skill

import committee.nova.skillful.impl.skill.instance.SkillInstance
import net.minecraft.entity.ai.attributes.{Attribute, AttributeModifier}
import net.minecraft.entity.player.ServerPlayerEntity

trait IApplyAttributeModifiers extends IActOnLevelChange with ICheckOnLogin {
  def getAttributeModifiers(player: ServerPlayerEntity, skillInstance: SkillInstance): Map[Attribute, AttributeModifier]

  def applyModifiers(player: ServerPlayerEntity, instance: SkillInstance): Unit = {
    for (m <- getAttributeModifiers(player, instance)) {
      val attr = m._1
      val modifier = m._2
      val pAttr = player.getAttribute(attr)
      pAttr.removeModifier(modifier.getId)
      pAttr.addPermanentModifier(modifier)
    }
  }

  override def act(player: ServerPlayerEntity, instance: SkillInstance, isUp: Boolean): Unit = applyModifiers(player, instance)

  override def check(player: ServerPlayerEntity, instance: SkillInstance): Unit = applyModifiers(player, instance)
}
