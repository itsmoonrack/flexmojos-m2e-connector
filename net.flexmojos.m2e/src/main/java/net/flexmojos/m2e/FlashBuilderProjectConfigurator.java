package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

/**
 * Configures a FlashBuilder project from Maven.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class FlashBuilderProjectConfigurator  extends
		AbstractProjectConfigurator {

	@Override
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
		
	}

}
