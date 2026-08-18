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
public final class LockAccessibilityService_MembersInjector implements MembersInjector<LockAccessibilityService> {
  private final Provider<LockSessionRepository> lockSessionRepositoryProvider;

  public LockAccessibilityService_MembersInjector(
      Provider<LockSessionRepository> lockSessionRepositoryProvider) {
    this.lockSessionRepositoryProvider = lockSessionRepositoryProvider;
  }

  public static MembersInjector<LockAccessibilityService> create(
      Provider<LockSessionRepository> lockSessionRepositoryProvider) {
    return new LockAccessibilityService_MembersInjector(lockSessionRepositoryProvider);
  }

  @Override
  public void injectMembers(LockAccessibilityService instance) {
    injectLockSessionRepository(instance, lockSessionRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.lockin.service.LockAccessibilityService.lockSessionRepository")
  public static void injectLockSessionRepository(LockAccessibilityService instance,
      LockSessionRepository lockSessionRepository) {
    instance.lockSessionRepository = lockSessionRepository;
  }
}
