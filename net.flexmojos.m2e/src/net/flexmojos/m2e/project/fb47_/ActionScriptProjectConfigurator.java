package net.flexmojos.m2e.project.fb47_;

import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.flexmojos.m2e.flex.FlexCompilerArguments;
import net.flexmojos.m2e.flex.FlexFrameworkHelper;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;
import com.adobe.flexbuilder.util.FlashPlayerVersion;
import com.google.inject.Inject;

public class ActionScriptProjectConfigurator implements IProjectConfigurator {

  protected IMavenProjectFacade facade;
  protected IProgressMonitor monitor;
  protected IProjectManager manager;
  protected IMutableActionScriptProjectSettings settings;

  protected ActionScriptProjectConfigurator() {}

  @Inject
  public ActionScriptProjectConfigurator(final IMavenProjectFacade facade, final IProgressMonitor monitor, final IProjectManager manager) {
    this.facade = facade;
    this.monitor = monitor;
    this.manager = manager;
    //    this.settings = manager.createFlexProjectDescription(facade.getProject(), false);
  }

  @Override
  public void configure() throws CoreException {
    new ActionScriptProjectSettings(
        facade.getProject().getName(),
        facade.getProject().getLocation(),
        false /* FIXME: overrideHTMLWrapperDefault */);
    configureMainSourceFolder();
    configureSourcePath();

    ((ActionScriptProjectSettings) settings).saveDescription(facade.getProject(), monitor);
  }

  protected Artifact getFlexFrameworkArtifact() {
    return facade.getMavenProject().getArtifactMap().get("com.adobe.flex.framework:flex-framework");
  }

  /**
   * @return Xpp3Dom
   *   Configuration.
   */
  protected Xpp3Dom getConfiguration() {
    final Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();
    Plugin plugin = null;

    if (plugins.containsKey("net.flexmojos.oss:flexmojos-maven-plugin")) {
      plugin = plugins.get("net.flexmojos.oss:flexmojos-maven-plugin");
    }
    else if (plugins.containsKey("org.sonatype.flexmojos:flexmojos-maven-plugin")) {
      return (Xpp3Dom) plugins.get("org.sonatype.flexmojos:flexmojos-maven-plugin").getConfiguration();
    }
    else if (plugins.containsKey("org.apache.maven.plugins:maven-flex-plugin")) {
      return (Xpp3Dom) plugins.get("org.apache.maven.plugins:maven-flex-plugin").getConfiguration();
    }

    return (Xpp3Dom) plugin.getConfiguration();
  }

  /**
   * Configures the main source folder.
   */
  protected void configureMainSourceFolder() {
    final Build build = facade.getMavenProject().getBuild();
    final IPath sourceDirectory = facade.getProjectRelativePath(build.getSourceDirectory());
    settings.setMainSourceFolder(sourceDirectory);
  }

  /**
   * Configures the source path so the testSourceDirectory, and additional
   * resources locations such as default src/main/resources are added to the
   * class path.
   */
  protected void configureSourcePath() {
    final List<IClassPathEntry> classPath = new ArrayList<IClassPathEntry>();

    // The test source directory is treated as a supplementary source path entry.
    final Build build = facade.getMavenProject().getBuild();
    final IPath testSourceDirectory = facade.getProjectRelativePath(build.getTestSourceDirectory());
    classPath.add(ClassPathEntryFactory.newEntry(testSourceDirectory.toString(), settings));

    final IPath[] resources = facade.getResourceLocations();
    for (int i = 0; i < resources.length; i++) {
      classPath.add(ClassPathEntryFactory.newEntry(resources[i].toString(), settings));
    }
    settings.setSourcePath(classPath.toArray(new IClassPathEntry[classPath.size()]));
  }

  /**
   * Configures the target player version, if no version is found, pass the
   * special string "0.0.0" who has the effect of toggling off the version
   * check.
   * 
   * @param configuration
   */
  protected void configureTargetPlayerVersion(final Xpp3Dom configuration) {
    final Xpp3Dom targetPlayer = configuration.getChild("targetPlayer");
    final String formattedVersionString = (targetPlayer != null) ? targetPlayer.getValue() : "0.0.0";
    settings.setTargetPlayerVersion(new FlashPlayerVersion(formattedVersionString));
  }

  /**
   * Configures the main application path, if no source file is found, use the
   * default which is inferred from project's name.
   * 
   * @param configuration
   */
  protected void configureMainApplicationPath(final Xpp3Dom configuration) {
    final Xpp3Dom sourceFile = configuration.getChild("sourceFile");
    if (sourceFile != null) {
      final IPath mainApplicationPath = new Path(sourceFile.getValue());
      settings.setApplicationPaths(new IPath[]{mainApplicationPath});
      settings.setMainApplicationPath(mainApplicationPath);
    }
  }

  /**
   * Configures the Flex SDK name and adds it to the library path of the project.
   * 
   * Must be called before configuring the library path.
   * 
   * @param flexFramework
   */
  protected void configureFlexSDKName() {
    final Artifact flexFramework = getFlexFrameworkArtifact();
    settings.setFlexSDKName(FlexFrameworkHelper.getFlexSDKName(flexFramework.getVersion()));
  }

  /**
   * Configures the library path by adding Maven's SWC dependencies of the project.
   * 
   * Must be called after configured the Flex SDK name.
   * 
   * @param settings
   * @see configureFlexSDKName
   */
  protected void configureLibraryPath() {
    final List<IClassPathEntry> dependencies = new ArrayList<IClassPathEntry>(Arrays.asList(settings.getLibraryPath()));
    for (final Artifact dependency : facade.getMavenProject().getArtifacts()) {
      // Only manage SWC type dependencies.
      if (SWC.equals(dependency.getType())
          // TODO: Adds a better condition handling: isNotFlash|Flex|AirFramework.
          && !dependency.getGroupId().equals("com.adobe.air.framework")
          && !dependency.getGroupId().equals("com.adobe.flex.framework")
          && !dependency.getGroupId().equals("com.adobe.flash.framework")) {
        final String path  = dependency.getFile().getAbsolutePath();
        dependencies.add(ClassPathEntryFactory.newEntry(IClassPathEntry.KIND_LIBRARY_FILE, path, settings));
      }
    }
    settings.setLibraryPath(dependencies.toArray(new IClassPathEntry[dependencies.size()]));
  }

  protected void configureAdditionalCompilerArgs(final Xpp3Dom configuration) {
    final FlexCompilerArguments arguments = new FlexCompilerArguments();

    // Sets source-path argument.
    final List<String> pathElements = new LinkedList<String>();
    final Xpp3Dom resourceBundlePath = configuration.getChild("resourceBundlePath");
    if (resourceBundlePath != null) {
      pathElements.add(facade.getProjectRelativePath(resourceBundlePath.getValue()).toString());
    }
    else {

    }
    arguments.setSourcePath(pathElements);

    // Sets locale argument.
    final List<String> locales = new LinkedList<String>();
    final Xpp3Dom localesCompiled = configuration.getChild("localesCompiled");
    if (localesCompiled != null) {
      for (final Xpp3Dom locale : localesCompiled.getChildren()) {
        locales.add(locale.getValue());
      }
    }
    arguments.setLocalesCompiled(locales);

    settings.setAdditionalCompilerArgs(arguments.toString());
  }

}
