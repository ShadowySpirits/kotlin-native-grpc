package moe.lv5.grpc

import com.squareup.wire.GrpcCall
import com.squareup.wire.GrpcClient
import com.squareup.wire.GrpcMethod
import com.squareup.wire.GrpcStreamingCall
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.Timeout

class KtorGrpcClient(private val client: HttpClient) : GrpcClient() {
    override fun <S : Any, R : Any> newCall(method: GrpcMethod<S, R>): GrpcCall<S, R> {
        return KtorCall(client, method, mapOf(), mapOf(), Timeout())
    }

    override fun <S : Any, R : Any> newStreamingCall(method: GrpcMethod<S, R>): GrpcStreamingCall<S, R> {
        TODO("Not yet implemented")
    }
}

class KtorCall<S : Any, R : Any>(
    private val client: HttpClient,
    override val method: GrpcMethod<S, R>,
    override var requestMetadata: Map<String, String>,
    override val responseMetadata: Map<String, String>?,
    override val timeout: Timeout
) : GrpcCall<S, R> {
    private lateinit var response: HttpResponse

    override fun cancel() {
        response.call.cancel()
    }

    override fun clone(): GrpcCall<S, R> {
        TODO("Not yet implemented")
    }

    override fun isCanceled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun executeBlocking(request: S): R {
        return runBlocking {
            response = client.request(buildRequest(request))

            val body = Buffer().write(response.readBytes())

            val compressedFlag = body.readByte()
            println("compressedFlag: $compressedFlag")

            val encodedLength = body.readInt().toLong() and 0xffffffffL
            println("encodedLength: $encodedLength")

            val encodedMessage = body.readByteArray(encodedLength)
            method.responseAdapter.decode(encodedMessage)
        }
    }

    override suspend fun execute(request: S): R {
        response = client.request(buildRequest(request))
        return method.responseAdapter.decode(response.readBytes())
    }

    override fun enqueue(request: S, callback: GrpcCall.Callback<S, R>) {
        TODO("Not yet implemented")
    }

    private fun buildRequest(request: S): HttpRequestBuilder {
        val grpcMethod = method
        return HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url {
                protocol = URLProtocol.HTTPS
                host = "localhost"
                port = 8081
                encodedPath = grpcMethod.path
            }
            headers {
                append(HttpHeaders.ContentType, "application/grpc")
                append("te", "trailers")
                append("grpc-trace-bin", "")
                requestMetadata.forEach {
                    append(it.key, it.value)
                }
            }
            val encodedMessage = Buffer()
            grpcMethod.requestAdapter.encode(encodedMessage, request)

            val buffer = Buffer()
            buffer.writeByte(0)
            buffer.writeInt(encodedMessage.size.toInt())
            buffer.writeAll(encodedMessage)
            setBody(ByteArrayContent(buffer.readByteArray()))
        }
    }
}