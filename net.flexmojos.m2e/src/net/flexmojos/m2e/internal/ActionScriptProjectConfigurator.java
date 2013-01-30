package net.flexmojos.m2e.internal;

import org.apache.maven.model.Build;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;

public class ActionScriptProjectConfigurator extends AbstractProjectConfigurator {
  
  IMavenProjectFacade facade;

  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();

    ActionScriptProjectSettings settings = new ActionScriptProjectSettings(
        project.getName(),
        project.getLocation(),
        false /* FIXME: overrideHTMLWrapperDefault */);

    configureMainSourceFolder(settings);
    configureSourcePath(settings);

    settings.saveDescription(project, monitor);
  }

  public void configureMainSourceFolder(IMutableActionScriptProjectSettings settings) {
    Build build = facade.getMavenProject().getBuild();
    IPath sourceDirectory = facade.getProjectRelativePath(build.getSourceDirectory());
    settings.setMainSourceFolder(sourceDirectory);
  }

  public void configureSourcePath(IMutableActionScriptProjectSettings settings) {
    Build build = facade.getMavenProject().getBuild();
    IPath testSourceDirectory = facade.getProjectRelativePath(build.getTestSourceDirectory());
    IPath[] resources = facade.getResourceLocations();
    IClassPathEntry[] classPath = new IClassPathEntry[1 + resources.length];
    // The test source directory is treated as a supplementary source path entry.
    classPath[0] = ClassPathEntryFactory.newEntry(testSourceDirectory.toString(), settings);
    for (int i = 0; i < resources.length; i++) {
      classPath[1 + i] = ClassPathEntryFactory.newEntry(resources[i].toString(), settings);
    }
    settings.setSourcePath(classPath);
  }
  
  public void addMarker(String message) {
    markerManager.addMarker(facade.getProject(), IMavenConstants.MARKER_CONFIGURATION_ID, message, -1, IMarker.SEVERITY_INFO);
  }

}
