package io.ch0wdren.application.service

import io.ch0wdren.application.port.usecase.UserUsecase
import io.ch0wdren.infrastructure.postgresql.ConnectionPool
import io.ch0wdren.infrastructure.postgresql.UserRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val factoryModule =
  module {
    factoryOf(::ServiceFactory) {
      bind<UserServiceFactory>()
    }
  }

interface UserServiceFactory {
  suspend fun createUserService(): UserUsecase
}

class ServiceFactory : UserServiceFactory {
  private val pool = ConnectionPool

  override suspend fun createUserService(): UserUsecase =
    UserService(
      pool.getConnection(),
      UserRepositoryImpl(),
    )
}
