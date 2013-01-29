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

import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;

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
		// Checks the project belongs to Flash/Flex/Air packaging.
		if (!isQualifiedAsFlashBuilderProject(facade)) {
			return;
		}

		AbstractProjectConfigurator configurator = null;
		
		if (isFlexProject(facade)) {
			// Depending on the packaging, a Flex project can be a FlexLibraryProject, or a FlexProject. The use case
			// for an Apollo project fall in the SWF or AIR packaging, which adds the flex nature to the project.
			if (SWC.equals(facade.getPackaging())) {
				addNature(project, "com.adobe.flexbuilder.project.flexlibnature", monitor);
				configurator = new FlexLibraryProjectConfigurator();
			}
			else {
				addNature(project, "com.adobe.flexbuilder.project.flexnature", monitor);
				if (AIR.equals(facade.getPackaging())) {
					// An ApolloProject inherits from a FlexProject.
					configurator = new ApolloProjectConfigurator();
				}
				else {
					// Else it is a pure FlexProject.
					configurator = new FlexProjectConfigurator();
				}
			}
		}
		else {
			// If the packaging is set to SWC, with no declared flex dependencies, then its an ActionScriptProject with
			// an aslib nature.
			if (SWC.equals(facade.getPackaging())) {
				addNature(project, "com.adobe.flexbuilder.project.aslibnature", monitor);
			}
		}

		if (isApolloProject(facade)) {
			// An Apollo project exists in two flavors: ApolloActionScriptProject, and ApolloProject. While the former
			// is directly extending from ActionScriptProject, the later inherits from FlexProject, so it is perfectly
			// possible for an Apollo project to be a Flex project as well.
			addNature(project, "com.adobe.flexbuilder.project.apollonature", monitor);
			configurator = new ApolloActionScriptProjectConfigurator();
		}

		// Every projects have the actionscript nature.
		addNature(project, "com.adobe.flexbuilder.project.actionscriptnature", monitor);
		if (configurator == null) {
			// If no project type has been found, it is a pure ActionScriptProject.
			configurator = new ActionScriptProjectConfigurator();
		}
		
		configurator.setMarkerManager(markerManager);
		configurator.setMavenConfiguration(mavenConfiguration);
		configurator.setProjectManager(projectManager);
		configurator.configure(request, monitor);

		// TODO Delegate this to respective project configurators.

//		Build build = request.getMavenProject().getBuild();
//
//		IPath sourceDirectory = facade.getProjectRelativePath(build.getSourceDirectory());
//		IPath testSourceDirectory = facade.getProjectRelativePath(build.getTestSourceDirectory());
//		// Source folder.
//		projectSettings.setMainSourceFolder(sourceDirectory);
//
//		// Class path entries.
//		IPath[] resources = facade.getResourceLocations();
//		IClassPathEntry[] classPath = new IClassPathEntry[1 + resources.length];
//
//		// The test source directory is treated as a supplementary source path entry.
//		classPath[0] = ClassPathEntryFactory.newEntry(testSourceDirectory.toString(), projectSettings);
//		for (int i = 0; i < resources.length; i++) {
//			classPath[1 + i] = ClassPathEntryFactory.newEntry(resources[i].toString(), projectSettings);
//		}
//		projectSettings.setSourcePath(classPath);
//
//		projectSettings.saveDescription(project, monitor);
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
	
	public ActionScriptProjectSettings configureActionScriptProject(ProjectConfigurationRequest request, IProgressMonitor monitor) {
		return null;
	}

}
