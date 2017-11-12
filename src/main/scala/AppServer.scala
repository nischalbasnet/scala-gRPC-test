import java.util.logging.Logger

import com.google.inject.{Guice, Injector}
import com.nischal.protos.hello._
import com.typesafe.config.{Config, ConfigFactory}
import greeting.GreetingController
import io.grpc.{Server, ServerBuilder, ServerServiceDefinition}
import net.ceedubs.ficus.Ficus._

import scala.concurrent.ExecutionContext

/**
  * App server object
  */
object AppServer
{
  private val logger: Logger = Logger.getLogger(getClass.getName)
  private val defaultPort = 5001

  /**
    * main function to run the server
    *
    * @param args : ignore
    */
  def main(args: Array[String]): Unit =
  {
    //load the configuration
    val config: Config = ConfigFactory.load()

    val server = new AppServer(ExecutionContext.global, config)
    server.start()
    server.blockUntilShutdown()
  }
}

/**
  * Handles the app server configuration and starting
  *
  * @param executionContext : context for server to run on
  * @param config           : application configuration
  */
class AppServer(executionContext: ExecutionContext, config: Config)
{
  self =>

  import net.codingwell.scalaguice.InjectorExtensions._

  //variable to hold the server
  private[this] var server: Option[Server] = None

  /**
    * Start the server
    *
    * @return
    */
  private def start() =
  {
    //get the injector
    val injector = Guice.createInjector(new ServerModule())

    //build the server
    val serverPort = config.as[Option[Int]]("app.http.port").getOrElse(AppServer.defaultPort)
    val serverBuilder = ServerBuilder.forPort(serverPort)
    services(injector).foreach(s => {
      serverBuilder.addService(s)
    })
    server = Some(
      serverBuilder
        .build()
        .start()
    )
    AppServer.logger.info(s"Server started, listening on $serverPort")
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("*** server shut down")
    }
  }

  /**
    * Get the list of services to bind with application server
    *
    * @param injector : Guice injector to get the instance of the services
    * @return
    */
  def services(injector: Injector): Seq[ServerServiceDefinition] =
  {
    Seq(
      GreetingGrpc.bindService(injector.instance[GreetingController], executionContext)
    )
  }

  /**
    * Stop the server
    */
  private def stop(): Unit =
  {
    for (s <- server) {
      s.shutdown()
    }
  }

  /**
    * keep the server alive until terminated
    */
  private def blockUntilShutdown(): Unit =
  {
    for (s <- server) {
      s.awaitTermination()
    }
  }

}
