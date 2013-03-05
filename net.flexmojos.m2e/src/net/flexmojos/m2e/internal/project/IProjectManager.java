package net.flexmojos.m2e.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;

/**
 * Defines the IProjectManager interface.
 * 
 * @TODO Ideally, the IMutableActionScriptProjectSettings should be an adapter, but because extracting all methods is
 * cumbersome, we use here the fb47 interface directly. However, the architecture can easily support other versions of
 * fb. Replacing the settings by an independant adapter class would solve the issue.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public interface IProjectManager {

  /**
   * Returns a new instance of a Flex project settings.
   * 
   * @param project The project.
   * @param overrideHTMLWrapperDefault
   * @return
   */
  public abstract IMutableFlexProjectSettings createFlexProjectDescription(
      IProject project, boolean overrideHTMLWrapperDefault);

  /**
   * Saves a project settings.
   * 
   * @param project
   * @param settings
   * @param monitor
   * @throws CoreException
   */
  public abstract void saveDescription(IProject project,
      IMutableActionScriptProjectSettings settings, IProgressMonitor monitor)
      throws CoreException;

}