package io.github.irevive

trait IntegrationEnv {
  def create(): Unit
  def terminate(): Unit

  final def prepare(strategy: TerminationStrategy): Unit =
    strategy.fold(
      uponTestCompletion = {
        terminate()
        create()
      },
      onExit = create(),
      never = create()
    )

}

trait IntegrationEnvProvider {
  def name: String
  def create(strategy: TerminationStrategy): Either[EnvConfigurationError, IntegrationEnv]
}
