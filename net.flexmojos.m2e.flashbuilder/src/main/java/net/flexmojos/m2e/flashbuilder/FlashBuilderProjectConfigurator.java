package net.flexmojos.m2e.flashbuilder;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.adobe.flexbuilder.project.actionscript.IActionScriptProjectSettings;
import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;

/**
 * Configures a FlashBuilder project from Maven.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class FlashBuilderProjectConfigurator extends
		AbstractProjectConfigurator {

	@Override
	public void configure(ProjectConfigurationRequest request,
			IProgressMonitor monitor) throws CoreException {
		IProject project = request.getProject();
		
		// Check the project belongs to Flash/Flex/Air packaging.
		final String packaging = request.getMavenProject().getPackaging();
		if (!Arrays.asList(new String[]{AIR, SWC, SWF}).contains(packaging)) {
			// If the project is not concerned, terminates.
			return;
		}
		IPath file = ActionScriptProjectSettings.AS_SETTINGS_FILE;
		IActionScriptProjectSettings settings = ActionScriptProjectSettings.getProjectSettingsFromFile(null);
		
		// Adds Flex and ActionScript project natures.
		addNature(project, "com.adobe.flexbuilder.project.flexnature", monitor);
		addNature(project, "com.adobe.flexbuilder.project.actionscriptnature", monitor);
		
	}

}
