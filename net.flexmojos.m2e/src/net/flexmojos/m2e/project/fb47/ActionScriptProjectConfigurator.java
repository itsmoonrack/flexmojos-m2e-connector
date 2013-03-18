package net.flexmojos.m2e.project.fb47;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.flexmojos.m2e.flex.FlexCompilerArguments;
import net.flexmojos.m2e.flex.FlexFrameworkHelper;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.project.AbstractConfigurator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.util.FlashPlayerVersion;
import com.google.inject.Inject;

public class ActionScriptProjectConfigurator extends AbstractConfigurator {

  protected IProject project;
  protected IProgressMonitor monitor;
  protected IMutableActionScriptProjectSettings settings;

  protected ActionScriptProjectConfigurator(final IMavenFlexPlugin plugin) {
    super(plugin);
  }

  @Inject
  public ActionScriptProjectConfigurator(final IMavenProjectFacade facade, final IProgressMonitor monitor, final IMavenFlexPlugin plugin) {
    this(plugin);
    this.monitor = monitor;
    this.project = facade.getProject();
    this.settings = ActionScriptCore.createProjectDescription(
        project.getName(),
        project.getLocation(),
        false /* FIXME: hard-coded! */);
  }

  @Override
  public void saveDescription() {
  }

  /**
   * Transforms IPath[] to IClassPathEntry[] array.
   * 
   * @param path
   * @return
   */
  protected IClassPathEntry[] transformIPath(final IPath[] path) {
    final IClassPathEntry[] classPath = new IClassPathEntry[path.length];

    for (int i = 0; i < path.length; i++)
      // Converts IPath to IClassPathEntry.
      classPath[i] = ClassPathEntryFactory.newEntry(path[i].toString(), settings);

    return classPath;
  }

  @Override
  public void configureMainSourceFolder() {
    settings.setMainSourceFolder(plugin.getMainSourceFolder());
  }

  @Override
  public void configureSourcePath() {
    settings.setSourcePath(transformIPath(plugin.getSourcePath()));
  }

  @Override
  public void configureTargetPlayerVersion() {
    final String playerBinary = plugin.getTargetPlayerVersion();
    final FlashPlayerVersion version = new FlashPlayerVersion(playerBinary == null ? "0.0.0" : playerBinary);
    settings.setTargetPlayerVersion(version);
  }

  @Override
  public void configureMainApplicationPath() {
    final IPath mainApplicationPath = plugin.getMainApplicationPath();
    if (mainApplicationPath != null) {
      settings.setApplicationPaths(new IPath[]{mainApplicationPath});
      settings.setMainApplicationPath(mainApplicationPath);
    }
  }

  @Override
  public void configureFlexSDKName() {
    final String flexVersion = plugin.getFlexFrameworkArtifact().getVersion();
    final String flexSDKName = FlexFrameworkHelper.getFlexSDKName(flexVersion);
    settings.setFlexSDKName(flexSDKName);
  }

  @Override
  public void configureLibraryPath() {
    // Gets the Flex SDK dependency added by configureFlexSDKName().
    final List<IClassPathEntry> dependencies = new ArrayList<IClassPathEntry>(Arrays.asList(settings.getLibraryPath()));
    dependencies.addAll(Arrays.asList(transformIPath(plugin.getLibraryPath())));
    settings.setLibraryPath(dependencies.toArray(new IClassPathEntry[dependencies.size()]));
  }

  @Override
  public void configureAdditionalCompilerArgs() {
    final FlexCompilerArguments arguments = new FlexCompilerArguments();

    // Sets source-path argument.
    final List<String> pathElements = new LinkedList<String>();
    pathElements.add(plugin.getLocalesSourcePath().toString());
    arguments.setSourcePath(pathElements);

    // Sets locale argument.
    final List<String> locales = new ArrayList<String>();
    locales.addAll(Arrays.asList(plugin.getLocalesCompiled()));
    arguments.setLocalesCompiled(locales);

    settings.setAdditionalCompilerArgs(arguments.toString());
  }

}
