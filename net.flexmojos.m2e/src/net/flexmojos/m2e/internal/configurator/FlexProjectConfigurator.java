package net.flexmojos.m2e.internal.configurator;

import java.util.Map;

import net.flexmojos.m2e.internal.project.IProjectManager;

import org.apache.maven.model.Plugin;
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

    Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();
    Xpp3Dom configuration = (Xpp3Dom) plugins.get("net.flexmojos.oss:flexmojos-maven-plugin").getConfiguration();
    if (configuration != null) {
      configureTargetPlayerVersion(configuration);
      configureMainApplicationPath(configuration);
//      configureAdditionalCompilerArgs(settings);
    }

    manager.saveDescription(project, settings, monitor);
  }

}
