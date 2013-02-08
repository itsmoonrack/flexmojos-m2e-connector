package net.flexmojos.m2e.internal.configurator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.air.IMutableApolloProjectSettings;
import com.adobe.flexbuilder.project.air.internal.ApolloProjectSettings;
import com.google.inject.Inject;

public class ApolloProjectConfigurator extends FlexProjectConfigurator {

  @Inject
  public ApolloProjectConfigurator(IMavenProjectFacade facade, IMutableApolloProjectSettings settings) {
    super(facade, settings);
  }

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    IMavenProjectFacade facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    ApolloProjectSettings projectSettings = new ApolloProjectSettings(
        project.getName(),
        project.getLocation(),
        FlexServerType.NO_SERVER /* Since its a Maven project, the server is on another module. */);
  }

}
