package moe.lv5

import apache.rocketmq.controller.v1.GrpcControllerServiceClient
import io.ktor.client.*
import io.ktor.client.engine.curl.*
import moe.lv5.grpc.KtorGrpcClient

fun getControllerClient(): GrpcControllerServiceClient {
    val client = HttpClient(Curl) {
        engine {
            sslVerify = false
        }
    }
    val grpcClient = KtorGrpcClient(client)
    return GrpcControllerServiceClient(grpcClient)
}