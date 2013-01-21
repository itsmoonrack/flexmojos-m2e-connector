package net.flexmojos.m2e.flashbuilder;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

/**
 * Used to configure a FlashBuilder project from Maven.
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
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		
		if (!Arrays.asList(natures).contains("com.adobe.flexbuilder.project.flexnature")) {
			Arrays.asList(natures).add("com.adobe.flexbuilder.project.flexnature");
		}
		
		if (!Arrays.asList(natures).contains("com.adobe.flexbuilder.project.actionscriptnature")) {
			Arrays.asList(natures).add("com.adobe.flexbuilder.project.actionscriptnature");
		}
		
		description.setNatureIds(natures);
		project.setDescription(description, monitor);
		
	}

}
