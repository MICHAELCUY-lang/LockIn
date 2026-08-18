package com.example.lockin.di;

import com.example.lockin.data.local.dao.LockSessionDao;
import com.example.lockin.domain.repository.LockSessionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideLockSessionRepositoryFactory implements Factory<LockSessionRepository> {
  private final Provider<LockSessionDao> daoProvider;

  public AppModule_ProvideLockSessionRepositoryFactory(Provider<LockSessionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public LockSessionRepository get() {
    return provideLockSessionRepository(daoProvider.get());
  }

  public static AppModule_ProvideLockSessionRepositoryFactory create(
      Provider<LockSessionDao> daoProvider) {
    return new AppModule_ProvideLockSessionRepositoryFactory(daoProvider);
  }

  public static LockSessionRepository provideLockSessionRepository(LockSessionDao dao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLockSessionRepository(dao));
  }
}
