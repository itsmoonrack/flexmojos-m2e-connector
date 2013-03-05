package net.flexmojos.m2e.internal.configurator;

import net.flexmojos.m2e.internal.project.IProjectManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.Inject;

public class FlexLibraryProjectConfigurator extends AbstractFlexProjectConfigurator {

  @Inject
  public FlexLibraryProjectConfigurator(IMavenProjectFacade facade, IProgressMonitor monitor, IProjectManager manager) {}

  public void configure() throws CoreException {}

}
