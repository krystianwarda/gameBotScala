//import org.scalatest.funsuite.AnyFunSuite
//import player.Player
//
//class PlayerTest extends AnyFunSuite {
//
//  test("updateState method should update character experience") {
//    val player = new Player()
//    player.updateState("charExperience", 1000)
//    assert(player.getExperienceValue == 1000)
//  }
//
//  test("updateState method should update character level") {
//    val player = new Player()
//    player.updateState("charLevel", 10)
//    assert(player.getLevelValue == 10)
//  }
//
//  test("updateState method should update health points") {
//    val player = new Player()
//    player.updateState("healthPoints", 50)
//    assert(player.getHealthPoints == 50)
//  }
//
//  test("updateState method should update mana points") {
//    val player = new Player()
//    player.updateState("manaPoints", 75)
//    assert(player.getMana == 75)
//  }
//
//  test("updateState method should update soul points") {
//    val player = new Player()
//    player.updateState("soulPoints", 25)
//    assert(player.getSoulPoints == 25)
//  }
//
//  test("updateState method should update capacity value") {
//    val player = new Player()
//    player.updateState("capacityValue", 80)
//    assert(player.getCapacityValue == 80)
//  }
//
//  test("updateState method should update magic level") {
//    val player = new Player()
//    player.updateState("magicLevel", 5)
//    assert(player.getMagicLevel == 5)
//  }
//
//  test("updateState method should handle invalid skill") {
//    val player = new Player()
//    player.updateState("invalidSkill", 123)
//    // We expect the method to print a message to the console, so we don't assert anything here
//  }
//}
