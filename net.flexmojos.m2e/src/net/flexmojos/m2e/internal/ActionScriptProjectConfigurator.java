package net.flexmojos.m2e.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

  /**
   * Configures the source path so the testSourceDirectory, and additional
   * resources locations such as default src/main/resources are added to the
   * class path.
   * 
   * @param settings
   */
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

  /**
   * Configures the target player version, if no version is found, pass the
   * special string "0.0.0" who has the effect of toggling off the version
   * check.
   * 
   * @param settings
   */
  protected void configureTargetPlayerVersion(IMutableFlexProjectSettings settings) {
    Xpp3Dom targetPlayer = configuration.getChild("targetPlayer");
    String formattedVersionString = (targetPlayer != null) ? targetPlayer.getValue() : "0.0.0";
    settings.setTargetPlayerVersion(new FlashPlayerVersion(formattedVersionString));
  }

  /**
   * Configures the main application path, if no source file is found, use the
   * default which is inferred from project's name.
   * 
   * @param settings
   */
  protected void configureMainApplicationPath(IMutableActionScriptProjectSettings settings) {
    Xpp3Dom sourceFile = configuration.getChild("sourceFile");
    if (sourceFile != null) {
      IPath mainApplicationPath = facade.getProjectRelativePath(sourceFile.getValue());
      settings.setMainApplicationPath(mainApplicationPath);
    }
  }

  protected void configureAdditionalCompilerArgs(IMutableActionScriptProjectSettings settings) {
    Map<String, Xpp3Dom> arguments = new FlexCompilerArguments();
    settings.setAdditionalCompilerArgs(arguments.toString());
  }

  private class FlexCompilerArguments extends HashMap<String, Xpp3Dom> {

    private String[] arguments = {
        "accessible",
        "actionscript-file-encoding"
    };

    private boolean[] argumentOperatorIsEqual = {
        true,
        false
    };

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FlexCompilerArguments() {
      Xpp3Dom value;
      // TODO: navigates through the configuration instead of the full array.
      for (Xpp3Dom config : configuration.getChildren()) {
      }
      // TODO: deprecate this.
      for (String key : arguments) {
        if ((value = configuration.getChild(toCamelCase(key.toCharArray()))) != null) {
          put(key, value);
        }
      }
    }

    private String toCamelCase(char[] c) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < c.length; i++) {
        if (c[i] == '-') {
          // Skip the hyphen and capitalize the next character.
          sb.append(Character.toUpperCase(c[++i]));
        }
        else
          sb.append(c[i]);
      }
      
      return sb.toString();
    }

    public String toString() {
      Iterator<Entry<String, Xpp3Dom>> i = entrySet().iterator();

      if (!i.hasNext())
        return "";

      StringBuilder sb = new StringBuilder();
      for (;;) {
        Entry<String, Xpp3Dom> e = i.next();
        String key = e.getKey();
        Xpp3Dom value = e.getValue();
        sb.append('-' + key);
        sb.append(' ');
        sb.append(value.getValue());
        if (!i.hasNext())
          return sb.toString();
        sb.append(' ');
      }

    }
  }

}
