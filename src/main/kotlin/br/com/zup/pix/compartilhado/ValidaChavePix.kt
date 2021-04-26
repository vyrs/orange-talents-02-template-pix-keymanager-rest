package br.com.zup.pix.compartilhado

import br.com.zup.pix.registra.NovaChaveRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ ValidPixKeyValidator::class ])
annotation class ValidaChavePix(
    val message: String = "chave Pix inválida (\${validatedValue.tipoDeChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidator: ConstraintValidator<ValidaChavePix, NovaChaveRequest> {

    override fun isValid(
        value: NovaChaveRequest?,
        annotationMetadata: AnnotationValue<ValidaChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {

        if (value?.tipoChave == null) {
            return false
        }

        return value.tipoChave.valida(value.chave)
    }

}