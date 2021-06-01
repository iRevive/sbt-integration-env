package io.github.irevive.client

import scala.sys.process.Process
import scala.util.Try

sealed abstract class DockerComposeClient(val name: String) {
  def up(dockerComposeFile: String, projectName: String, envVars: List[(String, String)]): Unit
  def down(dockerComposeFile: String, projectName: String, envVars: List[(String, String)]): Unit
}

object DockerComposeClient {

  object Docker extends DockerComposeClient("docker") {

    def up(dockerComposeFile: String, projectName: String, envVars: List[(String, String)]): Unit =
      Process(s"docker compose -f $dockerComposeFile -p $projectName up -d", None, envVars: _*).!

    def down(dockerComposeFile: String, projectName: String, envVars: List[(String, String)]): Unit =
      Process(s"docker compose -f $dockerComposeFile -p $projectName down", None, envVars: _*).!

  }

  object DockerCompose extends DockerComposeClient("docker-compose") {

    override def up(dockerComposeFile: String, projectName: String, envVars: List[(String, String)]): Unit =
      Process(s"docker-compose -f $dockerComposeFile -p $projectName up -d", None, envVars: _*).!

    override def down(dockerComposeFile: String, projectName: String, envVars: List[(String, String)]): Unit =
      Process(s"docker-compose -f $dockerComposeFile -p $projectName down", None, envVars: _*).!

  }

  def create: Either[Throwable, DockerComposeClient] =
    Try(Process("docker compose -h").!!)
      .map(_ => Docker)
      .orElse(Try(Process("docker-compose -h").!!).map(_ => DockerCompose))
      .toEither

}
