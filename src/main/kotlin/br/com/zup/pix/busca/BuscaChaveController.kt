package br.com.zup.pix.busca

import br.com.zup.BuscaChavePixRequest
import br.com.zup.ListaChavesPixRequest
import br.com.zup.PixBuscaGrpcServiceGrpc
import br.com.zup.PixListaGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/clientes/{clienteId}")
class BuscaChaveController(private val buscaChaveClient: PixBuscaGrpcServiceGrpc.PixBuscaGrpcServiceBlockingStub,
                           private val listaChaveClient: PixListaGrpcServiceGrpc.PixListaGrpcServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("chave/{pixId}")
    fun busca(clienteId: UUID, pixId: UUID): HttpResponse<Any> {

        logger.info("Buscando dados da chave pix: $pixId do cliente: $clienteId")

        val response = buscaChaveClient.busca(
            BuscaChavePixRequest.newBuilder()
                .setPixId(
                    BuscaChavePixRequest.FiltroPorPixId
                        .newBuilder()
                        .setPixId(pixId.toString())
                        .setClienteId(clienteId.toString())
                        .build()
                ).build()
        )

        return HttpResponse.ok(DetalhesChaveResponse(response))
    }

    @Get("chave")
    fun lista(clienteId: UUID): HttpResponse<Any> {

        logger.info("Buscando chaves do cliente: $clienteId")

        val response = listaChaveClient.lista(
            ListaChavesPixRequest
                .newBuilder()
                .setClienteId(clienteId.toString())
                .build()
        )

        val chaves = response.chavesList.map {  ChavePixResponse(it)  }
        return HttpResponse.ok(chaves)
    }
}