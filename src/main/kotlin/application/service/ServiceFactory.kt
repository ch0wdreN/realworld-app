package io.ch0wdren.application.service

import io.ch0wdren.application.UnitOfWork
import io.ch0wdren.application.port.usecase.ArticleUsecase
import io.ch0wdren.application.port.usecase.CommentUsecase
import io.ch0wdren.application.port.usecase.ProfileUsecase
import io.ch0wdren.application.port.usecase.TagUsecase
import io.ch0wdren.application.port.usecase.UserUsecase
import io.ch0wdren.infrastructure.postgresql.ArticleRepositoryImpl
import io.ch0wdren.infrastructure.postgresql.CommentRepositoryImpl
import io.ch0wdren.infrastructure.postgresql.ProfileRepositoryImpl
import io.ch0wdren.infrastructure.postgresql.TagRepositoryImpl
import io.ch0wdren.infrastructure.postgresql.UserRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val factoryModule =
  module {
    singleOf(::ServiceFactory) {
      bind<UserServiceFactory>()
      bind<ProfileServiceFactory>()
      bind<ArticleServiceFactory>()
      bind<CommentServiceFactory>()
      bind<TagServiceFactory>()
    }
  }

interface UserServiceFactory {
  suspend fun createUserService(): UserUsecase
}

interface ProfileServiceFactory {
  suspend fun createProfileService(): ProfileUsecase
}

interface ArticleServiceFactory {
  suspend fun createArticleService(): ArticleUsecase
}

interface CommentServiceFactory {
  suspend fun createCommentService(): CommentUsecase
}

interface TagServiceFactory {
  suspend fun createTagService(): TagUsecase
}

class ServiceFactory :
  UserServiceFactory,
  ProfileServiceFactory,
  ArticleServiceFactory,
  CommentServiceFactory,
  TagServiceFactory {
  override suspend fun createUserService(): UserUsecase =
    UserService(
      UnitOfWork(),
      UserRepositoryImpl(),
    )

  override suspend fun createProfileService(): ProfileUsecase =
    ProfileService(
      UnitOfWork(),
      ProfileRepositoryImpl(),
    )

  override suspend fun createArticleService(): ArticleUsecase =
    ArticleService(
      UnitOfWork(),
      ArticleRepositoryImpl(),
    )

  override suspend fun createCommentService(): CommentUsecase =
    CommentService(
      UnitOfWork(),
      CommentRepositoryImpl(),
    )

  override suspend fun createTagService(): TagUsecase =
    TagService(
      UnitOfWork(),
      TagRepositoryImpl(),
    )
}
