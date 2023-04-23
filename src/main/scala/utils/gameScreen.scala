package utils

object gameScreen {
  var windowName: String = "windowNameTemp"
  var windowId: String = "99999"
//  var windowImage: Mat

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


  def setWindowName(value: String): Unit = {
    windowName = value
  }
  def setWindowId(value: String): Unit = {
    windowId = value
  }

  // inventory location
  def setEquipment(eqSetting: String, value: Option[(Int, Int)]): Unit = {
    equipmentLoc = equipmentLoc + (eqSetting -> value)
  }

  def getEquipment(eqSetting: String): Option[(Int, Int)] = {
    equipmentLoc.getOrElse(eqSetting, throw new IllegalArgumentException("Invalid equipment setting"))
  }


}
