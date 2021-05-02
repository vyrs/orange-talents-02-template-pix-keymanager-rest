package br.com.zup.pix.deleta

import br.com.zup.DeletaChavePixRequest
import br.com.zup.PixDeletaGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/clientes/{clienteId}")
class DeletaChaveController(private val deletaChaveClient: PixDeletaGrpcServiceGrpc.PixDeletaGrpcServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Delete("chave/{pixId}")
    fun deleta(clienteId: UUID, pixId: UUID): HttpResponse<Any> {

        logger.info("Deletando chave pix: $pixId do cliente: $clienteId")

        deletaChaveClient.deleta(
            DeletaChavePixRequest.newBuilder()
                .setClientId(clienteId.toString())
                .setPixId(pixId.toString())
                .build()
        )

        return HttpResponse.ok()
    }

}