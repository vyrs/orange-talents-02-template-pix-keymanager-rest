package br.com.zup.pix.busca

import br.com.zup.*
import br.com.zup.pix.compartilhado.PixGrpcFactory
import com.google.protobuf.Timestamp
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class BuscaChaveControllerTest {

    @field:Inject
    lateinit var buscaStub: PixBuscaGrpcServiceGrpc.PixBuscaGrpcServiceBlockingStub

    @field:Inject
    lateinit var listaStub: PixListaGrpcServiceGrpc.PixListaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    val CHAVE_EMAIL = "vitor@mail.com.br"
    val CHAVE_CELULAR = "+5563984759841"
    val CONTA_CORRENTE = ContaEnum.CONTA_CORRENTE
    val TIPO_DE_CHAVE_EMAIL = ChaveEnum.EMAIL
    val TIPO_DE_CHAVE_CELULAR = ChaveEnum.CELULAR
    val INSTITUICAO = "Itau"
    val TITULAR = "Vitor"
    val DOCUMENTO_DO_TITULAR = "34597563067"
    val AGENCIA = "0001"
    val NUMERO_DA_CONTA = "1010-1"
    val CHAVE_CRIADA_EM = LocalDateTime.now().let {
        val criadoEm = it.atZone(ZoneId.of("UTC")).toInstant()
        Timestamp.newBuilder()
            .setSeconds(criadoEm.epochSecond)
            .setNanos(criadoEm.nano)
            .build()
    }

    @Test
    internal fun `deve buscar dados de uma chave`() {

        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        given(buscaStub.busca(Mockito.any())).willReturn(buscaChavePixResponse(clienteId, pixId))

        val request = HttpRequest.GET<Any>("/api/clientes/$clienteId/chave/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
    }

    @Test
    internal fun `deve buscar todas as chaves de um cliente`() {

        val clienteId = UUID.randomUUID().toString()

        given(listaStub.lista(Mockito.any())).willReturn(listaDeChavesResponse(clienteId))

        val request = HttpRequest.GET<Any>("/api/clientes/$clienteId/chave")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(response.body().size, 2)
    }

    private fun listaDeChavesResponse(clienteId: String): ListaChavesPixResponse {
        val chaveEmail = ListaChavesPixResponse.Chave.newBuilder()
                                .setPixId(UUID.randomUUID().toString())
                                .setChave(CHAVE_EMAIL)
                                .setTipoChave(TIPO_DE_CHAVE_EMAIL)
                                .setTipoConta(CONTA_CORRENTE)
                                .setCriadaEm(CHAVE_CRIADA_EM)
                                .build()

        val chaveCelular = ListaChavesPixResponse.Chave.newBuilder()
                                .setPixId(UUID.randomUUID().toString())
                                .setChave(CHAVE_CELULAR)
                                .setTipoChave(TIPO_DE_CHAVE_CELULAR)
                                .setTipoConta(CONTA_CORRENTE)
                                .setCriadaEm(CHAVE_CRIADA_EM)
                                .build()

        return ListaChavesPixResponse.newBuilder()
                                    .setClienteId(clienteId)
                                    .addAllChaves(listOf(chaveEmail, chaveCelular))
                                    .build()
    }

    private fun buscaChavePixResponse(clienteId: String, pixId: String) =
        BuscaChavePixResponse.newBuilder()
            .setPixId(pixId)
            .setClienteId(clienteId)
            .setDadosChave(
                BuscaChavePixResponse.Chave.newBuilder()
                    .setTipoChave(TIPO_DE_CHAVE_CELULAR)
                    .setChave(CHAVE_CELULAR)
                    .setTipoConta(CONTA_CORRENTE)
                    .setConta(
                        BuscaChavePixResponse.Chave.Conta.newBuilder()
                            .setInstituicao(INSTITUICAO)
                            .setNomeTitular(TITULAR)
                            .setCpfTitular(DOCUMENTO_DO_TITULAR)
                            .setAgencia(AGENCIA)
                            .setNumeroConta(NUMERO_DA_CONTA)
                            .build()
                    )
                    .setCriadaEm(CHAVE_CRIADA_EM)
            ).build()

    @Factory
    @Replaces(factory = PixGrpcFactory::class)
    internal class BuscaStubFactory {

        @Singleton
        fun buscaChave() = Mockito.mock(PixBuscaGrpcServiceGrpc.PixBuscaGrpcServiceBlockingStub::class.java)

        @Singleton
        fun ListaChaves() = Mockito.mock(PixListaGrpcServiceGrpc.PixListaGrpcServiceBlockingStub::class.java)
    }
}