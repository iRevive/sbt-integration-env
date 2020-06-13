package io.github.irevive

trait IntegrationEnv {
  def create(): Unit
  def terminate(): Unit
}

trait IntegrationEnvProvider {
  def name: String
  def create(mode: EnvMode): Either[EnvConfigurationError, IntegrationEnv]
}
