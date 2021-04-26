package br.com.zup.pix.compartilhado

import br.com.zup.PixBuscaGrpcServiceGrpc
import br.com.zup.PixDeletaGrpcServiceGrpc
import br.com.zup.PixListaGrpcServiceGrpc
import br.com.zup.PixRegistraGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class PixGrpcFactory(@GrpcChannel("projetoPix") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = PixRegistraGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deletaChave() = PixDeletaGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChaves() = PixListaGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun carregaChave() = PixBuscaGrpcServiceGrpc.newBlockingStub(channel)
}