package br.com.zup.pix.registra

import br.com.zup.PixRegistraGrpcServiceGrpc
import br.com.zup.RegistraChavePixResponse
import br.com.zup.pix.compartilhado.PixGrpcFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraChaveControllerTest {

    @field:Inject
    lateinit var registraStub: PixRegistraGrpcServiceGrpc.PixRegistraGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve registrar uma nova chave pix e retornar 201`() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val respostaGrpc = RegistraChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()

        given(registraStub.registra(Mockito.any())).willReturn(respostaGrpc)

        val novaChavePix = NovaChaveRequest(tipoConta = TipoContaRequest.CONTA_CORRENTE,
            chave = "vitor@mail.com.br",
            tipoChave = TipoChaveRequest.EMAIL
        )

        val request = HttpRequest.POST("/api/clientes/$clienteId/chave", novaChavePix)
        val response = client.toBlocking().exchange(request, NovaChaveRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId))
    }

    @Factory
    @Replaces(factory = PixGrpcFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = Mockito.mock(PixRegistraGrpcServiceGrpc.PixRegistraGrpcServiceBlockingStub::class.java)
    }
}