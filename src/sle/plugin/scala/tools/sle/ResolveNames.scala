package scala.tools.sle

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent
import scala.reflect.internal._

/** This class implements a plugin component using a tree
 *  traverser */
class ResolveNames (val global: Global) extends PluginComponent {
  import global._
  import global.definitions._

  val debug = false

  def report(a:Any) = {
    if (debug) println(a)
  }

  val runsAfter = List[String]("refchecks")
  /** The phase name of the compiler plugin
   */
  val phaseName = "sleResolveNames"

  def newPhase(prev: Phase): Phase = new TraverserPhase(prev)
  class TraverserPhase(prev: Phase) extends StdPhase(prev) {
    def apply(unit: CompilationUnit) {
      newTraverser().traverse(unit.body)
    }
  }

  def newTraverser(): Traverser = new ForeachTreeTraverser(check)

  def check(tree: Tree): Unit = tree match {
    case Apply(fun, args) =>
      report("traversing application of " + fun)
    case ClassDef(mods, name, tparams, impl) => {
      report("traversing class def " + name)
      report(tree.symbol.getAnnotations)
    }
    case _ => ()
  }
}
