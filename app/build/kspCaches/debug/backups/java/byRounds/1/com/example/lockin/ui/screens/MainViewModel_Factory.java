package com.example.lockin.ui.screens;

import com.example.lockin.data.local.DataStoreManager;
import com.example.lockin.domain.repository.AppRepository;
import com.example.lockin.domain.repository.LockSessionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<AppRepository> appRepositoryProvider;

  private final Provider<LockSessionRepository> lockSessionRepositoryProvider;

  private final Provider<DataStoreManager> dataStoreManagerProvider;

  public MainViewModel_Factory(Provider<AppRepository> appRepositoryProvider,
      Provider<LockSessionRepository> lockSessionRepositoryProvider,
      Provider<DataStoreManager> dataStoreManagerProvider) {
    this.appRepositoryProvider = appRepositoryProvider;
    this.lockSessionRepositoryProvider = lockSessionRepositoryProvider;
    this.dataStoreManagerProvider = dataStoreManagerProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(appRepositoryProvider.get(), lockSessionRepositoryProvider.get(), dataStoreManagerProvider.get());
  }

  public static MainViewModel_Factory create(Provider<AppRepository> appRepositoryProvider,
      Provider<LockSessionRepository> lockSessionRepositoryProvider,
      Provider<DataStoreManager> dataStoreManagerProvider) {
    return new MainViewModel_Factory(appRepositoryProvider, lockSessionRepositoryProvider, dataStoreManagerProvider);
  }

  public static MainViewModel newInstance(AppRepository appRepository,
      LockSessionRepository lockSessionRepository, DataStoreManager dataStoreManager) {
    return new MainViewModel(appRepository, lockSessionRepository, dataStoreManager);
  }
}
