package net.flexmojos.m2e.internal;

import static com.adobe.flexbuilder.project.IClassPathEntry.KIND_LIBRARY_FILE;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;
import com.adobe.flexbuilder.project.air.internal.ApolloProjectSettings;
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
    List<IClassPathEntry> classPath = new ArrayList<IClassPathEntry>();

    // The test source directory is treated as a supplementary source path entry.
    Build build = facade.getMavenProject().getBuild();
    IPath testSourceDirectory = facade.getProjectRelativePath(build.getTestSourceDirectory());
    classPath.add(ClassPathEntryFactory.newEntry(testSourceDirectory.toString(), settings));

    IPath[] resources = facade.getResourceLocations();
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
      IPath mainApplicationPath = new Path(sourceFile.getValue());
      settings.setApplicationPaths(new IPath[]{mainApplicationPath});
      settings.setMainApplicationPath(mainApplicationPath);
    }
  }

  /**
   * Returns a Flash Builder compatible framework name from flex-sdk-description.xml.
   * 
   * If no version is matching, simply returns "Flex X.Y.Z".
   * 
   * @param fullVersion
   * @return
   */
  private String getFlexSDKName(String fullVersion) {
    String version = fullVersion.substring(0, 5);
    String name = "Flex ";
    if (version.equals("4.5.1")) {
      name += "4.5.1A";
    }
    else if (version.equals("4.5.0")) {
      name += "4.5A";
    }
    else if (version.equals("4.1.0")) {
      name += "4.1A";
    }
    else if (version.equals("4.0.0")) {
      name += "4.0A";
    }
    else if (version.equals("3.6.0")) {
      name += "3.6A";
    }
    else if (version.equals("3.5.0")) {
      name += "3.5B";
    }
    else if (version.startsWith("3.4")) {
      name += "3.4A";
    }
    else if (version.equals("3.3.0")) {
      name += "3.3A";
    }
    else if (version.equals("3.2.0")) {
      name += "3.2A";
    }
    else if (version.startsWith("3.0")) {
      name += "3A";
    }
    else {
      name += version;
    }
    return name;
  }

  protected void configureFlexSDKName(IMutableActionScriptProjectSettings settings) {
    Artifact flexFramework = facade.getMavenProject().getArtifactMap().get("com.adobe.flex.framework:flex-framework");
    settings.setFlexSDKName(getFlexSDKName(flexFramework.getVersion()));
  }

  /**
   * Configures the library path by adding Maven's SWC dependencies of the project.
   * 
   * Must be called after having set the Flex SDK Name.
   * 
   * @param settings
   * @see configureFlexSDKName
   */
  protected void configureLibraryPath(IMutableActionScriptProjectSettings settings) {
    List<IClassPathEntry> dependencies = new ArrayList<IClassPathEntry>(Arrays.asList(settings.getLibraryPath()));
    for (Artifact dependency : facade.getMavenProject().getArtifacts()) {
      // Only manage SWC type dependencies.
      if (SWC.equals(dependency.getType())
          // TODO: Adds a better condition handling: isNotFlash|Flex|AirFramework.
          && !dependency.getGroupId().equals("com.adobe.air.framework")
          && !dependency.getGroupId().equals("com.adobe.flex.framework")
          && !dependency.getGroupId().equals("com.adobe.flash.framework")) {
        String path  = dependency.getFile().getAbsolutePath();
        dependencies.add(ClassPathEntryFactory.newEntry(KIND_LIBRARY_FILE, path, settings));
      }
    }
    settings.setLibraryPath(dependencies.toArray(new IClassPathEntry[dependencies.size()]));
  }

  protected void configureAdditionalCompilerArgs(IMutableActionScriptProjectSettings settings) {
    Map<String, Xpp3Dom> arguments = new FlexCompilerArguments();
    settings.setAdditionalCompilerArgs(arguments.toString());
  }

  private class FlexCompilerArguments extends HashMap<String, Xpp3Dom> {

    /**
     * Mxmlc compiler specifications.
     * 
     * @see http://help.adobe.com/en_US/flex/using/WS2db454920e96a9e51e63e3d11c0bf69084-7a92.html
     */
    private final Map<String, Character> arguments = new HashMap<String, Character>() {
      {put("accessible", '=');}
      {put("actionscript-file-encoding", ' ');}
      {put("allow-source-path-overlap", '=');}
      {put("as3", '=');}
      {put("benchmark", '=');}
      {put("compress", '=');}
      {put("context-root", ' ');}
      {put("contributor", ' ');}
      {put("creator", ' ');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
      {put("compress", '=');}
    };

    /**String[] arguments = {
        "accessible",
        "actionscript-file-encoding"
    };

    private boolean[] argumentOperatorIsEqual = {
        true,
        false
    };*/

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FlexCompilerArguments() {
      Xpp3Dom value;
      // TODO: navigates through the configuration instead of the full array.
      for (Xpp3Dom config : configuration.getChildren()) {
        String key = toHyphenKey(config.getName().toCharArray());
      }
    }

    private String toHyphenKey(char[] c) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < c.length; i++) {
        if (Character.isUpperCase(c[i])) {
          // Inserts a hyphen and lower case the character.
          sb.append("-" + Character.toLowerCase(c[i]));
        }
        else
          sb.append(c[i]);
      }

      return sb.toString();
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
