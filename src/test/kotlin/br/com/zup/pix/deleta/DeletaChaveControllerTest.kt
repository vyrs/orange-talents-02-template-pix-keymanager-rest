package br.com.zup.pix.deleta

import br.com.zup.DeletaChavePixResponse
import br.com.zup.PixDeletaGrpcServiceGrpc
import br.com.zup.pix.compartilhado.PixGrpcFactory
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DeletaChaveControllerTest {

    @field:Inject
    lateinit var deletaStub: PixDeletaGrpcServiceGrpc.PixDeletaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve deletar uma chave pix e retornar 200`() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val respostaGrpc = DeletaChavePixResponse.newBuilder()
            .setClientId(clienteId)
            .setPixId(pixId)
            .build()

        given(deletaStub.deleta(Mockito.any())).willReturn(respostaGrpc)


        val request = HttpRequest.DELETE<Any>("/api/clientes/$clienteId/chave/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Factory
    @Replaces(factory = PixGrpcFactory::class)
    internal class DeletaStubFactory {

        @Singleton
        fun deletaChave() = Mockito.mock(PixDeletaGrpcServiceGrpc.PixDeletaGrpcServiceBlockingStub::class.java)
    }
}