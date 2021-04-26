package br.com.zup.pix.registra

import br.com.caelum.stella.validation.CPFValidator
import br.com.zup.ChaveEnum
import br.com.zup.ContaEnum
import br.com.zup.RegistraChavePixRequest
import br.com.zup.pix.compartilhado.ValidaChavePix
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.validator.constraints.EmailValidator
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaChavePix
@Introspected
class NovaChaveRequest(@field:NotNull val tipoConta: TipoContaRequest?,
                       @field:Size(max = 77) val chave: String?,
                       @field:NotNull val tipoChave: TipoChaveRequest?) {

    fun paraRequestGrpc(clienteId: UUID): RegistraChavePixRequest {
        return RegistraChavePixRequest.newBuilder()
            .setClienteId(clienteId.toString())
            .setTipoConta(tipoConta?.atributoGrpc ?: ContaEnum.TIPO_CONTA_DESCONHECIDA)
            .setTipoChave(tipoChave?.atributoGrpc ?: ChaveEnum.TIPO_CHAVE_DESCONHECIDA)
            .setChave(chave ?: "")
            .build()
    }
}

enum class TipoChaveRequest(val atributoGrpc: ChaveEnum) {

    CPF(ChaveEnum.CPF) {

        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            return CPFValidator(false)
                .invalidMessagesFor(chave)
                .isEmpty()
        }

    },

    CELULAR(ChaveEnum.CELULAR) {
        override fun valida(chave: String?): Boolean {

            if (chave.isNullOrBlank()) {
                return false
            }
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },

    EMAIL(ChaveEnum.EMAIL) {

        override fun valida(chave: String?): Boolean {

            if (chave.isNullOrBlank()) {
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }

        }
    },

    ALEATORIA(ChaveEnum.ALEATORIA) {
        override fun valida(chave: String?) = chave.isNullOrBlank()
    };

    abstract fun valida(chave: String?): Boolean
}

enum class TipoContaRequest(val atributoGrpc: ContaEnum) {
    CONTA_CORRENTE(ContaEnum.CONTA_CORRENTE),
    CONTA_POUPANCA(ContaEnum.CONTA_POUPANCA)
}
