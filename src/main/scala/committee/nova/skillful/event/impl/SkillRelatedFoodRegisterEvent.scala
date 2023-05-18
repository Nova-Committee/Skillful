package committee.nova.skillful.event.impl

import committee.nova.skillful.api.related.ISkillRelatedFood
import committee.nova.skillful.storage.SkillfulStorage.addFood
import net.minecraftforge.eventbus.api.Event


class SkillRelatedFoodRegisterEvent extends Event {
  def addSkillRelatedFood(food: ISkillRelatedFood): Unit = addFood(food)

  def addSkillRelatedFoods(foods: ISkillRelatedFood*): Unit = for (food <- foods) addFood(food)
}
