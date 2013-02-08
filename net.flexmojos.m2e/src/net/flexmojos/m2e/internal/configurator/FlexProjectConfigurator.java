package net.flexmojos.m2e.internal.configurator;

import java.util.Map;

import net.flexmojos.m2e.internal.FlashBuilder47ProjectManager;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.google.inject.Inject;

public class FlexProjectConfigurator extends AbstractFlexProjectConfigurator {

  @Inject
  public FlexProjectConfigurator(IMavenProjectFacade facade, IMutableFlexProjectSettings settings) {
    super(facade, settings);
  }

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    IMavenProjectFacade facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    IMutableFlexProjectSettings settings = FlashBuilder47ProjectManager.createFlexProjectDescription(project, false /* FIXME: hard-coded. */);

    configureMainSourceFolder();
    configureSourcePath();
    configureFlexSDKName(null); // FIXME
    configureLibraryPath();

    Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();
    Xpp3Dom configuration = (Xpp3Dom) plugins.get("net.flexmojos.oss:flexmojos-maven-plugin").getConfiguration();
    if (configuration != null) {
      configureTargetPlayerVersion(configuration);
      configureMainApplicationPath(configuration);
//      configureAdditionalCompilerArgs(settings);
    }

    FlashBuilder47ProjectManager.saveDescription(project, settings, monitor);
  }

}
