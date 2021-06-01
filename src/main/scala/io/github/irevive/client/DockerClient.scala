package io.github.irevive.client

import scala.sys.process._
import scala.util.Try

sealed trait DockerClient {
  def version: String
  def createNetwork(network: String): Unit
}

object DockerClient {

  object Default extends DockerClient {

    override def version: String =
      s"docker version --format '{{.Server.Version}}'".!!

    override def createNetwork(network: String): Unit =
      s"docker network create $network".!
  }

  def create: Either[Throwable, DockerClient] =
    Try(Process("docker -v").!!).map(_ => Default).toEither

}
