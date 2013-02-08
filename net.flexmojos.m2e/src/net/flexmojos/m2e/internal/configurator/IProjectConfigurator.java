package net.flexmojos.m2e.internal.configurator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IProjectConfigurator {
  void configure(IProgressMonitor monitor) throws CoreException;
}
