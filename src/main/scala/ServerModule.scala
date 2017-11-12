import com.google.inject.AbstractModule
import com.nischal.Configuration
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule

/**
  * Module to bind the dependencies
  */
class ServerModule extends AbstractModule with ScalaModule
{
  override def configure(): Unit =
  {
    //bind the configuration
    val configuration = Configuration(ConfigFactory.load())
    bind[Configuration].toInstance(configuration)
  }
}
