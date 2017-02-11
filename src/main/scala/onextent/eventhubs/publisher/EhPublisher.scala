package onextent.eventhubs.publisher

import java.util.concurrent.CompletableFuture
import com.microsoft.azure.eventhubs.EventHubClient
import com.microsoft.azure.servicebus.ConnectionStringBuilder
import com.typesafe.config.ConfigFactory

object EhPublisher {

  private val config = ConfigFactory.load().getConfig("main")
  private val namespaceName = config.getString("eventhubs.eventHubNamespace")
  private val eventHubName = config.getString("eventhubs.eventHubName")
  private val sasKeyName = config.getString("eventhubs.policyName")
  private val sasKey = config.getString("eventhubs.policyKey")
  private val connStr = new ConnectionStringBuilder(namespaceName,
                                                    eventHubName,
                                                    sasKeyName,
                                                    sasKey)

  val f: CompletableFuture[EventHubClient] =
    EventHubClient.createFromConnectionString(connStr.toString)
  final val ehClient: EventHubClient = f.join()

}
