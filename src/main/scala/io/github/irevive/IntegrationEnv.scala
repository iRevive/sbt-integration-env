package io.github.irevive

import io.github.irevive.client._
import sbt._

sealed trait IntegrationEnv {
  def name: String
  def create(): Unit
  def terminate(): Unit

  final def prepare(strategy: TerminationStrategy): Unit =
    strategy.fold(
      uponTestCompletion = {
        terminate()
        create()
      },
      never = create()
    )

}

object IntegrationEnv {

  sealed abstract class Provider(val name: String) {
    def create(strategy: TerminationStrategy): Either[EnvConfigurationError, IntegrationEnv]
  }

  final case class DockerCompose(
      composeProjectName: String,
      dockerComposeFile: File,
      network: Option[String],
      dockerClient: DockerClient,
      composeClient: DockerComposeClient
  ) extends IntegrationEnv {

    override val name: String = DockerCompose.Name

    override def create(): Unit = {
      network.fold(())(net => dockerClient.createNetwork(net))
      composeClient.up(dockerComposeFile.toString, composeProjectName, dockerEnvVars)
    }

    override def terminate(): Unit =
      composeClient.down(dockerComposeFile.toString, composeProjectName, dockerEnvVars)

    protected def dockerEnvVars: List[(String, String)] =
      network.map(net => ("NETWORK", net)).toList

  }

  object DockerCompose {

    val Name = "docker-compose"

    final case class Provider(
        composeProjectName: String,
        dockerComposeFile: File,
        network: Option[String]
    ) extends IntegrationEnv.Provider(Name) {

      def create(strategy: TerminationStrategy): Either[EnvConfigurationError, IntegrationEnv] =
        for {
          dockerClient <- DockerClient.create.left.map(e => EnvConfigurationError.NotAvailable("docker", e.getMessage))

          composeClient <- DockerComposeClient.create.left.map(e =>
            EnvConfigurationError.NotAvailable("docker-compose", e.getMessage)
          )

          _ <- Either.cond(
            test = dockerComposeFile.exists(),
            right = (),
            left = EnvConfigurationError.Misconfigured(s"$dockerComposeFile - does not exist")
          )
        } yield DockerCompose(composeProjectName, dockerComposeFile, network, dockerClient, composeClient)

    }

  }

}
