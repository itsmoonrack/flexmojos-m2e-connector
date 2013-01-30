package net.flexmojos.m2e.internal;

import java.util.Map;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;

public class FlexProjectConfigurator extends AbstractFlexProjectConfigurator {

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    IMutableFlexProjectSettings settings = FlexProjectManager.createFlexProjectDescription(
        project.getName(),
        project.getLocation(),
        false /* FIXME: overrideHTMLWrapperDefault */,
        FlexServerType.NO_SERVER /* Since its a Maven project, the server is on another module. */);

    configureMainSourceFolder(settings);
    configureSourcePath(settings);

    Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();
    configuration = (Xpp3Dom) plugins.get("net.flexmojos.oss:flexmojos-maven-plugin").getConfiguration();
    if (configuration != null) {
      configureTargetPlayerVersion(settings);
    }

    FlexProjectManager.getFlexProject(project).setProjectDescription(settings, monitor);
  }

}
