package com.nischal

import com.typesafe.config.Config

/**
  * Contains the type safe config to be used across the app
  *
  * @param config : type safe config
  */
case class Configuration(
  config: Config
)
