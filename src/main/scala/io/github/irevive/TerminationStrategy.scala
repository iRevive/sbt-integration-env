package io.github.irevive

sealed trait TerminationStrategy extends Product with Serializable {

  def fold[A](uponTestCompletion: => A, never: => A): A =
    this match {
      case TerminationStrategy.UponTestCompletion => uponTestCompletion
      case TerminationStrategy.Never              => never
    }

}

object TerminationStrategy {

  /** Terminate after execution of tests
    */
  final case object UponTestCompletion extends TerminationStrategy

  /** Never terminate
    */
  final case object Never extends TerminationStrategy

}
