package greeting

import javax.inject.Singleton

/**
  * Handles the actions to be performed for GreetingController request
  */
@Singleton
class GreetingService
{
  /**
    * @param name : name used for greeting
    * @return
    */
  def greet(name: String) = s"Hello $name. How are you today?"
}
