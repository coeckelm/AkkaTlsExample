package com.ing.api.config

import akka.actor.ActorSystem
import com.typesafe.config.Config
import com.typesafe.sslconfig.util.EnrichedConfig

abstract class SettingsRoot

trait SettingsRootCompanion[T <: SettingsRoot] {

  protected def path: String

  protected def makeSettings(config: EnrichedConfig): T

  def apply(system: ActorSystem): T = apply(system.settings.config)

  def apply(config: Config): T = makeSettings(EnrichedConfig(config.getConfig(path)))
}
