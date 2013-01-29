package net.flexmojos.m2e.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.internal.FlexLibraryProjectSettings;

public class FlexLibraryProjectConfigurator extends AbstractFlexProjectConfigurator {

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    IMavenProjectFacade facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    FlexLibraryProjectSettings projectSettings = new FlexLibraryProjectSettings(
        project.getName(),
        project.getLocation(),
        false /* FIXME: overrideHTMLWrapperDefault */,
        false /* FIXME: useMultiPlatformConfig */,
        false /* FIXME: isFCCompatible */,
        false /* FIXME: isASOnly */);
  }

}
