package scope.lunit.rabbitmq.config

interface RabbitMQConfigValue {
    companion object {
        const val ONE_QUEUE_NAME = "workerOne"
        const val ONE_ROUTING_KEY = "worker.one"
        const val TWO_QUEUE_NAME = "workerTwo"
        const val TWO_ROUTING_KEY = "worker.two"
        const val TOPIC_EXCHANGE_NAME = "task-exchange"
    }

}