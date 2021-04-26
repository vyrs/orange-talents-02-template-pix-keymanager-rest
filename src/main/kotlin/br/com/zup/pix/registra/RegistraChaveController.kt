package br.com.zup.pix.registra

import br.com.zup.PixRegistraGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.Valid

@Validated
@Controller("/api/clientes/{clienteId}")
class RegistraChaveController(private val registraChaveClient: PixRegistraGrpcServiceGrpc.PixRegistraGrpcServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Post("chave")
    fun registra(clienteId: UUID, @Valid @Body request: NovaChaveRequest): HttpResponse<Any> {
        logger.info("Criando chave pix com: $request")

        val response = registraChaveClient.registra(request.paraRequestGrpc(clienteId))

        return HttpResponse.created(location(clienteId, response.pixId))
    }

    private fun location(clienteId: UUID, pixId: String) = HttpResponse
        .uri("/api/clientes/$clienteId/chave/$pixId")
}