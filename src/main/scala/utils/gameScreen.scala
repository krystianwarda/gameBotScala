package utils

object gameScreen {
  var screenName: String = "windowName"

  // inventory
  var equipmentLoc: Map[String, Option[(Int, Int)]] = Map(
    "inventoryLoc" -> None,
    "helmetLoc" -> None,
    "amuletLoc" -> None,
    "backpackLoc" -> None,
    "armorLoc" -> None,
    "lefthandLoc" -> None,
    "righthandLoc" -> None,
    "legsLoc" -> None,
    "ringLoc" -> None,
    "arrowLoc" -> None,
    "bootsLoc" -> None
  )

  def setScreenName(value: String): Unit = {
    screenName = value
  }

  // inventory location
  def setEquipment(eqSetting: String, value: Option[(Int, Int)]): Unit = {
    equipmentLoc = equipmentLoc + (eqSetting -> value)
  }

  def getEquipment(eqSetting: String): Option[(Int, Int)] = {
    equipmentLoc.getOrElse(eqSetting, throw new IllegalArgumentException("Invalid equipment setting"))
  }

}
