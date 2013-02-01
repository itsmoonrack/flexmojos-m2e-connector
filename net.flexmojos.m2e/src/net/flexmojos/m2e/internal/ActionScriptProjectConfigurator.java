package net.flexmojos.m2e.internal;

import org.apache.maven.model.Build;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;
import com.adobe.flexbuilder.project.compiler.CompilerArgs;
import com.adobe.flexbuilder.util.FlashPlayerVersion;

public class ActionScriptProjectConfigurator extends AbstractProjectConfigurator {

  Xpp3Dom configuration;
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

  protected void configureMainSourceFolder(IMutableActionScriptProjectSettings settings) {
    Build build = facade.getMavenProject().getBuild();
    IPath sourceDirectory = facade.getProjectRelativePath(build.getSourceDirectory());
    settings.setMainSourceFolder(sourceDirectory);
  }

  protected void configureSourcePath(IMutableActionScriptProjectSettings settings) {
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

  protected void configureTargetPlayerVersion(IMutableFlexProjectSettings settings) {
    Xpp3Dom targetPlayer = configuration.getChild("targetPlayer");
    String formattedVersionString = (targetPlayer != null) ? targetPlayer.getValue() : "0.0.0";
    settings.setTargetPlayerVersion(new FlashPlayerVersion(formattedVersionString));
  }
  
  protected void configureMainApplicationPath(IMutableActionScriptProjectSettings settings) {
    Xpp3Dom sourceFile = configuration.getChild("sourceFile");
    if (sourceFile != null) {
      IPath mainApplicationPath = facade.getProjectRelativePath(sourceFile.getValue());
      settings.setMainApplicationPath(mainApplicationPath);
    }
  }

  protected void configureAdditionalCompilerArgs(IMutableActionScriptProjectSettings settings) {
    StringBuilder arguments = new StringBuilder();

    Xpp3Dom services = configuration.getChild("services");
    if (services != null) {
      arguments.append("-services " + services.getValue());
    }

    settings.setAdditionalCompilerArgs(arguments.toString());
  }

}
