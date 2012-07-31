import sle.annotations._

@params("R")
class RegionParamClass {
  val x = new RegionParamClass @args("R")
}

object Test {
  def main(args: Array[String]) = {}
}
