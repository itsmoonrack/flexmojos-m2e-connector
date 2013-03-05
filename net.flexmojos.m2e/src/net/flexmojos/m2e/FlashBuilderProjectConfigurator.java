package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;

import java.util.Arrays;

import net.flexmojos.m2e.internal.FlashBuilderModule;
import net.flexmojos.m2e.internal.configurator.IProjectConfigurator;
import net.flexmojos.m2e.internal.project.FB47ProjectManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
    if (!isQualifiedAsFlashBuilderProject(facade)) {
      return;
    }

    // Creates the project configurator through the FlashBuilder47Module.
    Injector injector = Guice.createInjector(new FlashBuilderModule(facade, monitor, FB47ProjectManager.class));
    IProjectConfigurator configurator = injector.getInstance(IProjectConfigurator.class);

    configurator.configure();
  }

  private boolean isQualifiedAsFlashBuilderProject(IMavenProjectFacade facade) {
    return Arrays.asList(new String[]{AIR, SWC, SWF}).contains(facade.getPackaging());
  }

}
