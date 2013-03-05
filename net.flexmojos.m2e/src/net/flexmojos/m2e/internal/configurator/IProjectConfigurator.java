package net.flexmojos.m2e.internal.configurator;

import org.eclipse.core.runtime.CoreException;

public interface IProjectConfigurator {
  void configure() throws CoreException;
}
