package org.specs2
package control

import Eff._
import Effects._
import StateEffect._
import scalaz._, Scalaz.{get =>_, put =>_, _}

class StateEffectSpec extends Specification with ScalaCheck { def is = s2"""

 The state monad can be used to modify state $modifyState

 The Eff monad is stack safe with State $stacksafeState

"""

  def modifyState = {
    val action: Eff[E, String] = for {
      a <- get[E, Int]
      h <- EffMonad[E].point("hello")
      _ <- put[E, Int](a + 5)
      b <- get
      _ <- put(b + 10)
      w <- EffMonad[E].point("world")
    } yield h+" "+w

    run(StateEffect.runState(5)(action)) ==== (("hello world", 20))
  }

  def stacksafeState = {
    val list = (1 to 5000).toList
    val action = list.traverseU(i => StateEffect.put[E, Int](i).as(i.toString))

    run(StateEffect.runState(0)(action)) ==== ((list.map(_.toString), 5000))
  }

  type StateInt[A] = State[Int, A]
  type E = StateInt |: NoEffect
  implicit def StateIntMember: Member[StateInt, E] =
    Member.MemberNatIsMember

}