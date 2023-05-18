package committee.nova.skillful.event.impl

import committee.nova.skillful.api.skill.ISkill
import committee.nova.skillful.storage.SkillfulStorage
import net.minecraftforge.eventbus.api.Event


class SkillRegisterEvent extends Event {
  def addSkill(skill: ISkill): Unit = SkillfulStorage.addSkill(skill)

  def addSkills(skills: ISkill*): Unit = for (skill <- skills) addSkill(skill)
}
