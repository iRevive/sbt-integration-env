package io.github.irevive

import sbt._

import scala.sys.process._

class DockerComposeEnv(
    composeProjectName: String,
    dockerComposeFile: File,
    network: Option[String]
) extends IntegrationEnv {

  override def create(): Unit = {
    network.fold(())(net => s"docker network create $net".!)
    Process(s"docker-compose -f $dockerComposeFile -p $composeProjectName up -d", None, dockerEnvVars: _*).!
  }

  override def terminate(): Unit =
    Process(s"docker-compose -f $dockerComposeFile -p $composeProjectName down", None, dockerEnvVars: _*).!

  protected def dockerEnvVars: Seq[(String, String)] =
    network.map(net => ("NETWORK", net)).toSeq

}

class DockerComposeEnvProvider(
    composeProjectName: String,
    dockerComposeFile: File,
    network: Option[String]
) extends IntegrationEnvProvider {

  override val name: String = "docker-compose"

  override def create(strategy: TerminationStrategy): Either[EnvConfigurationError, IntegrationEnv] =
    for {
      _ <- Either.cond("docker-compose -version".! == 0, (), EnvConfigurationError.NotAvailable)
      _ <- Either.cond(
        test = dockerComposeFile.exists(),
        right = (),
        left = EnvConfigurationError.Misconfigured(s"$dockerComposeFile - does not exist")
      )
    } yield new DockerComposeEnv(composeProjectName, dockerComposeFile, network)

}
