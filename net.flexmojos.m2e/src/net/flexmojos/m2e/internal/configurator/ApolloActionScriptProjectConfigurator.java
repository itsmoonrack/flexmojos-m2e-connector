package net.flexmojos.m2e.internal.configurator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.air.internal.ApolloActionScriptProjectSettings;
import com.google.inject.Inject;

public class ApolloActionScriptProjectConfigurator extends ActionScriptProjectConfigurator {

  @Inject
  public ApolloActionScriptProjectConfigurator(IMavenProjectFacade facade, IMutableActionScriptProjectSettings settings) {
    super(facade, settings);
  }

  public void configure(IProgressMonitor monitor) throws CoreException {
    IProject project = facade.getProject();

    ApolloActionScriptProjectSettings projectSettings = new ApolloActionScriptProjectSettings(
        project.getName(),
        project.getLocation());
  }

}
