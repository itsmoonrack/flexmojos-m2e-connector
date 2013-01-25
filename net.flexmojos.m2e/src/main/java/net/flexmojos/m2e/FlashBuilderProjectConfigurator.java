package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
public class FlashBuilderProjectConfigurator  extends
AbstractProjectConfigurator {

	public void configure(ProjectConfigurationRequest request,
			IProgressMonitor monitor) throws CoreException {
		// Check the project belongs to Flash/Flex/Air packaging.
		final String packaging = request.getMavenProject().getPackaging();

		if (!Arrays.asList(new String[]{AIR, SWC, SWF}).contains(packaging)) {
			// If the project is not concerned, terminates.
			return;
		}

		// Adds Flex and ActionScript project natures.
		addNature(request.getProject(), "com.adobe.flexbuilder.project.flexnature", monitor);
		addNature(request.getProject(), "com.adobe.flexbuilder.project.actionscriptnature", monitor);

		IProject project = request.getProject();
		Build build = request.getMavenProject().getBuild();

		String sourceDirectory = alignToProjectDirectory(project, build.getSourceDirectory());
		String testSourceDirectory = alignToProjectDirectory(project, build.getTestSourceDirectory());
		//		ActionScriptProjectSettings baseSettings = new ActionScriptProjectSettings(project.getName(), project.getLocation(), false);
		//		baseSettings.setMainSourceFolder(new Path("src/main/flex"));
		//		baseSettings.saveDescription(project, monitor);
		FlexProjectSettings settings = new FlexProjectSettings(project.getName(), project.getLocation(), false, FlexServerType.NO_SERVER);
		// Source folder.
		settings.setMainSourceFolder(new Path(sourceDirectory));

		// Class path entries.
		List<Resource> resources = request.getMavenProject().getResources();
		IClassPathEntry[] classPath = new IClassPathEntry[1 + resources.size()];

		classPath[0] = ClassPathEntryFactory.newEntry(testSourceDirectory, settings);
		for (int i = 0; i < resources.size(); i++) {
			String directory = alignToProjectDirectory(project, resources.get(i).getDirectory());
			classPath[1 + i] = ClassPathEntryFactory.newEntry(directory, settings);
		}
		settings.setSourcePath(classPath);

		settings.saveDescription(project, monitor);
	}

	/**
	 * Resolves the specified path against the given project directory. The resolved path will be relative and uses the
	 * platform-specific file separator if a base directory is given. Otherwise, the input path will be returned
	 * unaltered.
	 * 
	 * @param project
	 * @param path
	 * @return
	 * @throws URISyntaxException 
	 */
	private String alignToProjectDirectory(IProject project, String fullPath) {
		IFile path = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fullPath));
		return path.getProjectRelativePath().toString();
	}

}
