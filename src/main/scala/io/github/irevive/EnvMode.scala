package io.github.irevive

sealed trait EnvMode extends Product with Serializable {

  def fold[A](ci: => A, dev: => A): A =
    this match {
      case EnvMode.CI  => ci
      case EnvMode.Dev => dev
    }

}

object EnvMode {
  final case object CI  extends EnvMode
  final case object Dev extends EnvMode
}
