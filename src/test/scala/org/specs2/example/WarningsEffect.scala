package org.specs2
package example

import org.specs2.control.Effects._
import org.specs2.control.{Effects, Eff, Member, WriterEffect}
import scalaz._

object WarningsEffect {

  trait WarningsTag

  type Warnings[A] = Writer[String, A] @@ WarningsTag

  /** warn the user about something that is probably wrong on his side, this is not a specs2 bug */
  def warn[R](message: String)(implicit m: Member[Warnings, R]): Eff[R, Unit] =
    WriterEffect.tell(message)(Member.untagMember[Writer[String, ?], R, WarningsTag](m))

  /**
   * This interpreter cumulates warnings
   */
  def runWarnings[R <: Effects, A](w: Eff[Warnings |: R, A]): Eff[R, (A, Vector[String])] =
    WriterEffect.runTaggedWriter[R, WarningsTag, String, A](w)

}
