package moe.lv5

import apache.rocketmq.controller.v1.CreateTopicRequest
import apache.rocketmq.controller.v1.MessageType
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int

class MQAdmin : CliktCommand() {
    private val endpoint: String by option().default("localhost:8081").help("Endpoint of RocketMQ broker")

    override fun run() {
    }
}

class CreateTopic : CliktCommand() {
    private val topic by option("-t", help = "Topic name").required()
    private val count by option("-c", help = "Queue count").int().default(1)
    private val type by option(help = "Message type").enum<MessageType>().default(MessageType.NORMAL)

    override fun run() {
        val controllerServiceClient = getControllerClient()

        val request = CreateTopicRequest(
            topic = topic,
            count = count,
            accept_message_types = listOf(type),
        )
        val createTopicReply = controllerServiceClient.createTopic()
            .executeBlocking(request)
        println(createTopicReply)
    }
}

fun main(args: Array<String>) = MQAdmin().subcommands(CreateTopic()).main(args)
