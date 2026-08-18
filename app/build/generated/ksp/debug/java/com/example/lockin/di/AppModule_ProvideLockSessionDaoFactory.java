package com.example.lockin.di;

import com.example.lockin.data.local.LockInDatabase;
import com.example.lockin.data.local.dao.LockSessionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AppModule_ProvideLockSessionDaoFactory implements Factory<LockSessionDao> {
  private final Provider<LockInDatabase> databaseProvider;

  public AppModule_ProvideLockSessionDaoFactory(Provider<LockInDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public LockSessionDao get() {
    return provideLockSessionDao(databaseProvider.get());
  }

  public static AppModule_ProvideLockSessionDaoFactory create(
      Provider<LockInDatabase> databaseProvider) {
    return new AppModule_ProvideLockSessionDaoFactory(databaseProvider);
  }

  public static LockSessionDao provideLockSessionDao(LockInDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLockSessionDao(database));
  }
}
