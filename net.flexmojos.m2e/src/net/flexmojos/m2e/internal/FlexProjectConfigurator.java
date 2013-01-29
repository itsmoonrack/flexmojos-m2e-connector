package net.flexmojos.m2e.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.internal.FlexProjectSettings;

public class FlexProjectConfigurator extends AbstractFlexProjectConfigurator {

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    IMavenProjectFacade facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    FlexProjectSettings projectSettings = new FlexProjectSettings(
        project.getName(),
        project.getLocation(),
        false /* FIXME: overrideHTMLWrapperDefault */,
        FlexServerType.NO_SERVER /* Since its a Maven project, the server is on another module. */);
  }

}
