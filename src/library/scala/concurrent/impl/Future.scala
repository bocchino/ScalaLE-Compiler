/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.concurrent.impl



import scala.concurrent.util.Duration
import scala.concurrent.{Awaitable, ExecutionContext, CanAwait}
import scala.collection.mutable.Stack
import scala.util.control.NonFatal


private[concurrent] trait Future[+T] extends scala.concurrent.Future[T] with Awaitable[T] {

}

private[concurrent] object Future {

  /** Wraps a block of code into an awaitable object. */
  private[concurrent] def body2awaitable[T](body: =>T) = new Awaitable[T] {
    def ready(atMost: Duration)(implicit permit: CanAwait) = {
      body
      this
    }
    def result(atMost: Duration)(implicit permit: CanAwait) = body
  }
  
  def boxedType(c: Class[_]): Class[_] = if (c.isPrimitive) scala.concurrent.Future.toBoxed(c) else c

  private[impl] class PromiseCompletingRunnable[T](body: => T)
    extends Runnable {
    val promise = new Promise.DefaultPromise[T]()

    override def run() = {
      promise complete {
        try Right(body) catch { case NonFatal(e) => Left(e) }
      }
    }
  }

  def apply[T](body: =>T)(implicit executor: ExecutionContext): Future[T] = {
    val runnable = new PromiseCompletingRunnable(body)
    executor.execute(runnable)
    runnable.promise.future
  }
}
