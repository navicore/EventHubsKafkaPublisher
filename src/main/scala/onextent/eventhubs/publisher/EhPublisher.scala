package onextent.eventhubs.publisher

import com.microsoft.azure.eventhubs.EventHubClient
import com.microsoft.azure.servicebus.ConnectionStringBuilder
import com.typesafe.config.ConfigFactory

object EhPublisher {

  private val config = ConfigFactory.load().getConfig("main")
  private val namespaceName = config.getString("eventhubs.eventHubNamespace")
  private val eventHubName = config.getString("eventhubs.eventHubName")
  private val sasKeyName = config.getString("eventhubs.policyName")
  private val sasKey = config.getString("eventhubs.policyKey")
  private val connStr = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey)
  final val ehClient = EventHubClient.createFromConnectionString(connStr.toString()).get
}

