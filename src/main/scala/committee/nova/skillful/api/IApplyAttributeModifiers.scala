package committee.nova.skillful.api

import committee.nova.skillful.skills.SkillInstance
import net.minecraft.entity.ai.attributes.{AttributeModifier, IAttribute}
import net.minecraft.entity.player.EntityPlayerMP

trait IApplyAttributeModifiers extends IActOnLevelChange with ICheckOnLogin {
  def getTargetAttribute: IAttribute

  def getAttributeModifiers(player: EntityPlayerMP, skillInstance: SkillInstance): Map[IAttribute, AttributeModifier]

  def applyModifiers(player: EntityPlayerMP, instance: SkillInstance): Unit = {
    for (m <- getAttributeModifiers(player, instance)) {
      val attr = m._1
      val modifier = m._2
      val pAttr = player.getEntityAttribute(attr)
      pAttr.removeModifier(modifier.getID)
      pAttr.applyModifier(modifier)
    }
  }

  override def act(player: EntityPlayerMP, instance: SkillInstance, isUp: Boolean): Unit = applyModifiers(player, instance)

  override def check(player: EntityPlayerMP, instance: SkillInstance): Unit = applyModifiers(player, instance)
}
