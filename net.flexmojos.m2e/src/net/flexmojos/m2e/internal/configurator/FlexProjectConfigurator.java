package net.flexmojos.m2e.internal.configurator;

import net.flexmojos.m2e.internal.project.IProjectManager;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.Inject;

public class FlexProjectConfigurator extends AbstractFlexProjectConfigurator {

  protected FlexProjectConfigurator() {}

  @Inject
  public FlexProjectConfigurator(IMavenProjectFacade facade, IProgressMonitor monitor, IProjectManager manager) {
    this.facade = facade;
    this.monitor = monitor;
    this.manager = manager;
    this.settings = manager.createFlexProjectDescription(facade.getProject(), false /* FIXME: hard-coded. */);
  }

  public void configure() throws CoreException {
    IProject project = facade.getProject();

    configureMainSourceFolder();
    configureSourcePath();
    configureFlexSDKName();
    configureLibraryPath();

    Xpp3Dom configuration = getConfiguration();
    if (configuration != null) {
      configureTargetPlayerVersion(configuration);
      configureMainApplicationPath(configuration);
      configureAdditionalCompilerArgs(configuration);
    }

    manager.saveDescription(project, settings, monitor);
  }

}
