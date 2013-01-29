package net.flexmojos.m2e.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;

public class ActionScriptProjectConfigurator extends AbstractProjectConfigurator {

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    IMavenProjectFacade facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    ActionScriptProjectSettings projectSettings = new ActionScriptProjectSettings(
        project.getName(),
        project.getLocation(),
        false /* FIXME: overrideHTMLWrapperDefault */);
  }

}
