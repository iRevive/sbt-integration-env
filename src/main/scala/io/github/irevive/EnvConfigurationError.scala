package io.github.irevive

sealed trait EnvConfigurationError extends Product with Serializable

object EnvConfigurationError {

  final case object MissingProvider                               extends EnvConfigurationError
  final case class NotAvailable(component: String, cause: String) extends EnvConfigurationError
  final case class Misconfigured(cause: String)                   extends EnvConfigurationError

}
