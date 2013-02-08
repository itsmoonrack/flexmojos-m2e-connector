package net.flexmojos.m2e.internal;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.util.Map;

import net.flexmojos.m2e.internal.configurator.ActionScriptProjectConfigurator;
import net.flexmojos.m2e.internal.configurator.ApolloActionScriptProjectConfigurator;
import net.flexmojos.m2e.internal.configurator.ApolloProjectConfigurator;
import net.flexmojos.m2e.internal.configurator.FlexLibraryProjectConfigurator;
import net.flexmojos.m2e.internal.configurator.FlexProjectConfigurator;
import net.flexmojos.m2e.internal.configurator.IProjectConfigurator;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;

import com.google.inject.AbstractModule;

/**
 * Flash Builder 4.7 components configuration.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class FlashBuilder47Module extends AbstractModule {

  private final IMavenProjectFacade facade;
  private final IProgressMonitor monitor;

  public FlashBuilder47Module(IMavenProjectFacade facade, IProgressMonitor monitor) {
    this.facade = facade;
    this.monitor = monitor;
  }

  @SuppressWarnings("unchecked")
  protected void configure() {
    final IProject project = facade.getProject();

    // Adds the ActionScript nature.
    addNature(project, "com.adobe.flexbuilder.project.actionscriptnature", monitor);
    // Sets the base project configurator to an ActionScript project configurator. While a project can have multiple
    // natures, a project can not have more than one configurator. The algorithm bellow is based on "the last
    // assignment is the right one" adding natures to the project as the execution flow goes into the branches but
    // overriding configurators to eventually define the project.
    @SuppressWarnings("rawtypes")
    Class configurator = ActionScriptProjectConfigurator.class;

    if (isApolloProject()) {
      // An Apollo project exists in two flavors: ApolloActionScriptProject, and ApolloProject. While the former
      // directly extends from ActionScriptProject, the later inherits from FlexProject, so it is perfectly
      // possible for an Apollo project to have a Flex nature as well.
      addNature(project, "com.adobe.flexbuilder.project.apollonature", monitor);
      // The configurator will replace the ActionScript project configurator initially set by an ApolloActionScript
      // project configurator. Later in the execution flow, in the case a project have the Flex nature as well, the
      // configurator will be replaced by a "pure" Apollo project configurator.
      configurator = ApolloActionScriptProjectConfigurator.class;
    }
    
    if (isFlexProject()) {
      // Depending on the packaging, a Flex project can be a FlexLibraryProject (SWC), a FlexProject (SWF) or an
      // ApolloProject (AIR).
      if (SWC.equals(facade.getPackaging())) {
        addNature(project, "com.adobe.flexbuilder.project.flexlibnature", monitor);
        configurator = FlexLibraryProjectConfigurator.class;
        // End of algorithm.
      }
      else {
        // An AIR and SWF packaging indicates respectively an ApolloProject and a FlexProject, in both case the Flex
        // nature is added to the project.
        addNature(project, "com.adobe.flexbuilder.project.flexnature", monitor);
        if (AIR.equals(facade.getPackaging())) {
          configurator = ApolloProjectConfigurator.class;
          // End of algorithm.
        }
        else {
          configurator = FlexProjectConfigurator.class;
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

    bind(IProjectConfigurator.class).to(configurator);
    bind(IMavenProjectFacade.class).toInstance(facade);
  }

  /**
   * Short-hand method for wrapping an "addNature" operation.
   * 
   * @param project
   * @param natureId
   * @param monitor
   */
  private void addNature(IProject project, String natureId, IProgressMonitor monitor) {
    try {
      AbstractProjectConfigurator.addNature(project, natureId, monitor);
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private boolean isFlexProject() {
    Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
    // Supports both Adobe and Apache groupId.
    return dependencies.containsKey("com.adobe.flex.framework:flex-framework")
        || dependencies.containsKey("org.apache.flex.framework:flex-framework");
  }

  private boolean isApolloProject() {
    // TODO: implement me !
    return false;
  }

}
