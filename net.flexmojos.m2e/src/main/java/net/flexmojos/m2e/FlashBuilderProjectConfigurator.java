package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.internal.FlexProjectSettings;

/**
 * Configures a FlashBuilder project from Maven.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class FlashBuilderProjectConfigurator  extends AbstractProjectConfigurator {

	/**
	 * Adds the Flash/Flex/Air nature to projects qualified as Flash Builder compatible, i.e,
	 * having a packaging of type "swc", "swf", or "air" in their pom.xml file.
	 */
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = request.getMavenProjectFacade();
		IProject project = facade.getProject();
		// Check the project belongs to Flash/Flex/Air packaging.
		if (!isQualifiedAsFlashBuilderProject(facade)) {
			return;
		}

		// TODO Move out of this method what is currently bellow this line.
		// Adds Flex and ActionScript project natures.
		addNature(request.getProject(), "com.adobe.flexbuilder.project.flexnature", monitor);
		addNature(request.getProject(), "com.adobe.flexbuilder.project.actionscriptnature", monitor);

		Build build = request.getMavenProject().getBuild();

		IPath sourceDirectory = facade.getProjectRelativePath(build.getSourceDirectory());
		IPath testSourceDirectory = facade.getProjectRelativePath(build.getTestSourceDirectory());
		FlexProjectSettings settings = new FlexProjectSettings(project.getName(), project.getLocation(), false, FlexServerType.NO_SERVER);
		// Source folder.
		settings.setMainSourceFolder(sourceDirectory);

		// Class path entries.
		IPath[] resources = facade.getResourceLocations();
		IClassPathEntry[] classPath = new IClassPathEntry[1 + resources.length];

		// The test source directory is treated as a supplementary source path entry.
		classPath[0] = ClassPathEntryFactory.newEntry(testSourceDirectory.toString(), settings);
		for (int i = 0; i < resources.length; i++) {
			classPath[1 + i] = ClassPathEntryFactory.newEntry(resources[i].toString(), settings);
		}
		settings.setSourcePath(classPath);

		settings.saveDescription(project, monitor);
	}

	private boolean isQualifiedAsFlashBuilderProject(IMavenProjectFacade facade) {
		return Arrays.asList(new String[]{AIR, SWC, SWF}).contains(facade.getPackaging());
	}

}
