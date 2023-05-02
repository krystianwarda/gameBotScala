package player

class Example(val name: String, val id: Int) {
  var randomVar1: Int = util.Random.nextInt(100)
  var randomVar2: Int = util.Random.nextInt(100)

  def updateVar(rv1: Int, rv2:Int): Unit = {
    randomVar1 = rv1
    randomVar2 = rv2
  }

  def this(name: String) {
    this(name, util.Random.nextInt(100))
  }

  override def toString: String = s"$name (ID: $id)"
}

