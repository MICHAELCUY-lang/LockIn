package com.example.lockin.service;

import com.example.lockin.domain.repository.LockSessionRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppMonitoringService_MembersInjector implements MembersInjector<AppMonitoringService> {
  private final Provider<LockSessionRepository> lockSessionRepositoryProvider;

  public AppMonitoringService_MembersInjector(
      Provider<LockSessionRepository> lockSessionRepositoryProvider) {
    this.lockSessionRepositoryProvider = lockSessionRepositoryProvider;
  }

  public static MembersInjector<AppMonitoringService> create(
      Provider<LockSessionRepository> lockSessionRepositoryProvider) {
    return new AppMonitoringService_MembersInjector(lockSessionRepositoryProvider);
  }

  @Override
  public void injectMembers(AppMonitoringService instance) {
    injectLockSessionRepository(instance, lockSessionRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.lockin.service.AppMonitoringService.lockSessionRepository")
  public static void injectLockSessionRepository(AppMonitoringService instance,
      LockSessionRepository lockSessionRepository) {
    instance.lockSessionRepository = lockSessionRepository;
  }
}
