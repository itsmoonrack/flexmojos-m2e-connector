package net.flexmojos.m2e.internal;

import java.util.Map;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;

public class FlexProjectConfigurator extends AbstractFlexProjectConfigurator {

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    IMutableFlexProjectSettings settings = ProjectManager.createFlexProjectDescription(project, false /* FIXME: hard-coded. */);

    configureMainSourceFolder(settings);
    configureSourcePath(settings);
    configureFlexSDKName(settings);
    configureLibraryPath(settings);

    Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();
    configuration = (Xpp3Dom) plugins.get("net.flexmojos.oss:flexmojos-maven-plugin").getConfiguration();
    if (configuration != null) {
      configureTargetPlayerVersion(settings);
      configureMainApplicationPath(settings);
//      configureAdditionalCompilerArgs(settings);
    }

    ProjectManager.saveDescription(project, settings, monitor);
  }

}
