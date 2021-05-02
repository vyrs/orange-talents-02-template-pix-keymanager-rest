package br.com.zup.pix.busca

import br.com.zup.BuscaChavePixResponse
import br.com.zup.ContaEnum
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class DetalhesChaveResponse(chaveResponse: BuscaChavePixResponse) {

    val pixId = chaveResponse.pixId
    val tipo = chaveResponse.dadosChave.tipoChave
    val chave = chaveResponse.dadosChave.chave

    val criadaEm = chaveResponse.dadosChave.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    val tipoConta = when (chaveResponse.dadosChave.tipoConta) {
        ContaEnum.CONTA_CORRENTE -> "CONTA_CORRENTE"
        ContaEnum.CONTA_POUPANCA -> "CONTA_POUPANCA"
        else -> "NAO_RECONHECIDA"
    }

    val conta = mapOf(Pair("tipo", tipoConta),
        Pair("instituicao", chaveResponse.dadosChave.conta.instituicao),
        Pair("nomeDoTitular", chaveResponse.dadosChave.conta.nomeTitular),
        Pair("cpfDoTitular", chaveResponse.dadosChave.conta.cpfTitular),
        Pair("agencia", chaveResponse.dadosChave.conta.agencia),
        Pair("numero", chaveResponse.dadosChave.conta.numeroConta))
}