# UNDER CONSTRUCTION - NOT WORKING QUITE YET

# temporarily inlining the src from the azure event hubs master repo (waiting on a jar for v2.0.3)

# EventHubs Kafka Publisher

using Spark v2.0.2 to write data from Kafka v0.10.x to an Azure Event Hub

## REQUIRED ENV VARS

```shell
    EH_POLICY_NAME=<CHANGE ME>
    EH_POLICY_KEY=<CHANGE ME>
    EH_NAMESPACE=<CHANGE ME>
    EH_HUB_NAME=<CHANGE ME>
    KAFKA_TOPIC=<CHANGE ME>
```

## OPTIONAL ENV VARS 

_the defaults work for some during dev but change these too_
```shell
    KAFKA_BATCH_DUR="2"
    KAFKA_BROKER_LIST="localhost:9092"
    KAFKA_CONSUMER_GROUP=<CHANGE ME>
```

## Build Jar

```shell
sbt assembly
```

## Build Docker Image

```shell
sbt assembly && docker build -t myimage .
```

## Deploy via Kubernetes

TODO

TODO

TODO
