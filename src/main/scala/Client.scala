import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import com.nischal.protos.hello.GreetingGrpc.GreetingBlockingStub
import com.nischal.protos.hello.{GreetingGrpc, HelloReq, HelloRes}
import com.typesafe.config.{Config, ConfigFactory}
import com.trueaccord.scalapb.GeneratedMessage
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import net.ceedubs.ficus.Ficus._

/**
  * Client object
  */
object Client
{
  /**
    * main function for taking users input and making request
    *
    * @param args : ignore
    */
  def main(args: Array[String]): Unit =
  {
    val config = ConfigFactory.load()
    val client = AppServerClient(config)
    var input = ""
    try {
      while (input.toLowerCase() != "exit") {
        input = scala.io.StdIn.readLine(s"${Console.GREEN}Enter name for request${Console.RED}:${Console.RESET}")
        client.sayHello(input)
      }
    }
    finally {
      client.shutdown()
    }
  }
}

/**
  * client class for making request to appserver
  */
case class AppServerClient private(
  channel: ManagedChannel,
  blockingStub: GreetingBlockingStub
)
{
  private[this] val logger = Logger.getLogger(getClass.getName)

  /**
    * close the client
    *
    * @return
    */
  def shutdown(): Boolean =
  {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
  }

  /**
    * method to call sayHello method in server
    *
    * @param name : name to be greeted with
    */
  def sayHello(name: String): Unit =
  {
    logger.info(s"request: $name")

    val request: HelloReq = HelloReq(name)
    try {
      //      val response: HelloRes = blockingStub.sayHello(request)
      val response: HelloRes = doCall(request, blockingStub.sayHello)
      logger.info(s"response: $response")
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, s"RPC request failed: ${e.getMessage}")
    }
  }

  /**
    * Handle all call with exception logging
    *
    * @param payload       : request payload
    * @param requestMethod : request method
    * @tparam PT : payload type
    * @tparam RT : response type
    * @return
    */
  private def doCall[PT <: GeneratedMessage, RT <: GeneratedMessage](payload: PT, requestMethod: PT => RT): RT =
  {
    try {
      requestMethod(payload)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, s"inner call: RPC request failed: ${e.getMessage}")
        throw e
    }
  }
}

/**
  * object for client
  */
object AppServerClient
{
  /**
    * create AppServerClient from config
    *
    * @param config : config to get info from
    * @return
    */
  def apply(config: Config): AppServerClient = AppServerClient(
    config.as[Option[String]]("app.http.host").getOrElse("localhost"),
    config.as[Option[Int]]("app.http.port").getOrElse(5001)
  )

  def apply(host: String, port: Int): AppServerClient =
  {
    //build the channel
    val channel = ManagedChannelBuilder
      .forAddress(host, port)
      .usePlaintext(true)
      .build
    //build the stub
    val blockingStub = GreetingGrpc.blockingStub(channel)
    new AppServerClient(channel, blockingStub)
  }
}
