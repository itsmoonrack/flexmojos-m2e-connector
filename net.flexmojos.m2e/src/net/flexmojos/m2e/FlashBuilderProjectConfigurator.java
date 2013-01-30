package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;

import java.util.Arrays;
import java.util.Map;

import net.flexmojos.m2e.internal.ActionScriptProjectConfigurator;
import net.flexmojos.m2e.internal.ApolloActionScriptProjectConfigurator;
import net.flexmojos.m2e.internal.ApolloProjectConfigurator;
import net.flexmojos.m2e.internal.FlexLibraryProjectConfigurator;
import net.flexmojos.m2e.internal.FlexProjectConfigurator;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

/**
 * Configures a FlashBuilder project from Maven.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class FlashBuilderProjectConfigurator extends AbstractProjectConfigurator {

  /**
   * Adds the Flash/Flex/Air nature to projects qualified as Flash Builder compatible, i.e,
   * having a packaging of type "swc", "swf", or "air" in their pom.xml file.
   * 
   * The configurator looks through the declared dependencies of the pom.xml file to infers the project type.
   */
  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    IMavenProjectFacade facade = request.getMavenProjectFacade();
    IProject project = facade.getProject();
    if (!isQualifiedAsFlashBuilderProject(facade)) {
      return;
    }

    // Adds the ActionScript nature.
    addNature(project, "com.adobe.flexbuilder.project.actionscriptnature", monitor);
    // Sets the base project configurator to an ActionScript project configurator. While a project can have multiple
    // natures, a project can not have more than one configurator. The algorithm bellow is based on "the last
    // assignment is the right one" adding natures to the project as the execution flow goes into the branches but
    // overriding configurators to eventually define the project.
    ActionScriptProjectConfigurator configurator = new ActionScriptProjectConfigurator();

    if (isApolloProject(facade)) {
      // An Apollo project exists in two flavors: ApolloActionScriptProject, and ApolloProject. While the former
      // directly extends from ActionScriptProject, the later inherits from FlexProject, so it is perfectly
      // possible for an Apollo project to have a Flex nature as well.
      addNature(project, "com.adobe.flexbuilder.project.apollonature", monitor);
      // The configurator will replace the ActionScript project configurator initially set by an ApolloActionScript
      // project configurator. Later in the execution flow, in the case a project have the Flex nature as well, the
      // configurator will be replaced by a "pure" Apollo project configurator.
      configurator = new ApolloActionScriptProjectConfigurator();
    }
    
    if (isFlexProject(facade)) {
      // Depending on the packaging, a Flex project can be a FlexLibraryProject (SWC), a FlexProject (SWF) or an
      // ApolloProject (AIR).
      if (SWC.equals(facade.getPackaging())) {
        addNature(project, "com.adobe.flexbuilder.project.flexlibnature", monitor);
        configurator = new FlexLibraryProjectConfigurator();
        // End of algorithm.
      }
      else {
        // An AIR and SWF packaging indicates respectively an ApolloProject and a FlexProject, in both case the Flex
        // nature is added to the project.
        addNature(project, "com.adobe.flexbuilder.project.flexnature", monitor);
        if (AIR.equals(facade.getPackaging())) {
          configurator = new ApolloProjectConfigurator();
          // End of algorithm.
        }
        else {
          configurator = new FlexProjectConfigurator();
          // End of algorithm.
        }
      }
    }
    else if (SWC.equals(facade.getPackaging())) {
      // In the case there is no declared Flex dependencies, and the packaging is SWC, its an ActionScriptProject with
      // an aslib nature.
      addNature(project, "com.adobe.flexbuilder.project.aslibnature", monitor);
      // End of algorithm.
    }

    configurator.setMarkerManager(markerManager);
    configurator.setMavenConfiguration(mavenConfiguration);
    configurator.setProjectManager(projectManager);
    configurator.configure(request, monitor);
  }

  private boolean isQualifiedAsFlashBuilderProject(IMavenProjectFacade facade) {
    return Arrays.asList(new String[]{AIR, SWC, SWF}).contains(facade.getPackaging());
  }

  private boolean isFlexProject(IMavenProjectFacade facade) {
    Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
    // Supports both Adobe and Apache groupId.
    return dependencies.containsKey("com.adobe.flex.framework:flex-framework")
        || dependencies.containsKey("org.apache.flex.framework:flex-framework");
  }

  private boolean isApolloProject(IMavenProjectFacade facade) {
    // TODO: implement me !
    return false;
  }

}
