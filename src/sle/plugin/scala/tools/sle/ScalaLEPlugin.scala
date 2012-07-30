package scala.tools.sle

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.Plugin

/** A class describing the compiler plugin
 *
 *  @todo Adapt the name of this class to the plugin being
 *  implemented
 */
class ScalaLEPlugin(val global: Global) extends Plugin {
  /** The name of this plugin. */
  val name = "sle"

  val runsAfter = List[String]("refchecks")

  /** A short description of the plugin. */
  val description = "Scala with Logical Effects"
  
  /** @todo A description of the plugin's options */
  override val optionsHelp = Some(
    "  -P:"+ name +":option     sets some option for this plugin")

  /** @todo Implement parsing of plugin options */
  override def processOptions(options: List[String], error: String => Unit) {
    super.processOptions(options, error)
  }

  /** The compiler components that will be applied when running
   *  this plugin
   *
   *  @todo Adapt to the plugin being implemented
   */
  val components = ScalaLEPlugin.components(global)

  val checker = new ScalaLEAnnotationChecker {
    val global: ScalaLEPlugin.this.global.type = ScalaLEPlugin.this.global
  }
  global.addAnnotationChecker(checker.checker)
}

object ScalaLEPlugin {
  /** Yields the list of Components to be executed in this plugin
   *
   *  @todo: Adapt to specific implementation.
   */
  def components(global: Global) =
    List(new ScalaLEComponent(global),
         new ScalaLETraverseComponent(global),
         new ScalaLETransformComponent(global),
         new ScalaLEInfoTransformComponent(global))
}
