package io.github.irevive

import io.github.irevive.IntegrationEnv.{DockerCompose, Provider}
import io.github.irevive.client.{DockerClient, DockerComposeClient}
import sbt.File
import sjsonnew.BasicJsonProtocol._
import sjsonnew._

object IntegrationEnvFormats {

  implicit val integrationEnvFormat: JsonFormat[IntegrationEnv] = new JsonFormat[IntegrationEnv] {

    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): IntegrationEnv =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val name = unbuilder.readField[String]("name")

          name match {
            case DockerCompose.Name =>
              val composeProjectName = unbuilder.readField[String]("composeProjectName")
              val dockerComposeFile  = unbuilder.readField[File]("dockerComposeFile")
              val network            = unbuilder.readField[Option[String]]("network")

              val dockerComposeClient = unbuilder.readField[String]("dockerComposeClient") match {
                case DockerComposeClient.Docker.name        => DockerComposeClient.Docker
                case DockerComposeClient.DockerCompose.name => DockerComposeClient.DockerCompose
                case other                                  => deserializationError(s"Unknown DockerComposeClient [$other]")
              }

              unbuilder.endObject()

              DockerCompose(composeProjectName, dockerComposeFile, network, DockerClient.Default, dockerComposeClient)

            case other =>
              deserializationError(s"Unknown IntegrationEnv [$other]")
          }

        case None =>
          deserializationError("Expected JsObject but found None")
      }

    def write[J](obj: IntegrationEnv, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("name", obj.name)

      obj match {
        case DockerCompose(composeProjectName, dockerComposeFile, network, _, dockerComposeClient) =>
          builder.addField("composeProjectName", composeProjectName)
          builder.addField("dockerComposeFile", dockerComposeFile)
          builder.addField("network", network)
          builder.addField("dockerComposeClient", dockerComposeClient.name)
      }

      builder.endObject()
    }
  }

  implicit val integrationEnvProviderFormat: JsonFormat[IntegrationEnv.Provider] = new JsonFormat[IntegrationEnv.Provider] {

    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Provider =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val name = unbuilder.readField[String]("name")

          name match {
            case DockerCompose.Name =>
              val composeProjectName = unbuilder.readField[String]("composeProjectName")
              val dockerComposeFile  = unbuilder.readField[File]("dockerComposeFile")
              val network            = unbuilder.readField[Option[String]]("network")

              unbuilder.endObject()

              DockerCompose.Provider(composeProjectName, dockerComposeFile, network)

            case other =>
              deserializationError(s"Unknown IntegrationEnv.Provider [$other]")
          }

        case None =>
          deserializationError("Expected JsObject but found None")
      }

    def write[J](obj: Provider, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("name", obj.name)

      obj match {
        case DockerCompose.Provider(composeProjectName, dockerComposeFile, network) =>
          builder.addField("composeProjectName", composeProjectName)
          builder.addField("dockerComposeFile", dockerComposeFile)
          builder.addField("network", network)
      }

      builder.endObject()
    }
  }

}
