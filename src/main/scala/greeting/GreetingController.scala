package greeting

import java.util.logging.Logger
import javax.inject.{Inject, Singleton}

import com.nischal.Configuration
import com.nischal.protos.hello._
import net.ceedubs.ficus.Ficus._

import scala.concurrent.Future

@Singleton
class GreetingController @Inject()(
  greetingService: GreetingService,
  configuration: Configuration
) extends GreetingGrpc.Greeting
{
  private[this] val logger = Logger.getLogger(getClass.getName)

  /**
    * Controller method to say hello
    *
    * @param request : controller request
    * @return
    */
  override def sayHello(request: HelloReq): Future[HelloRes] =
  {
    val port = configuration.config.as[Option[Int]]("app.http.port")

    logger.info(s"${Console.GREEN}requested with $request")
    logger.info(s"responding with service from port: $port${Console.RESET}")

    val response = HelloRes(message = greetingService.greet(request.name))
    Future.successful(response)
  }
}
