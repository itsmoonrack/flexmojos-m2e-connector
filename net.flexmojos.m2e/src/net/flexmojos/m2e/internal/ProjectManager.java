package net.flexmojos.m2e.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.FlexWorkspaceRunner;
import com.adobe.flexbuilder.project.IFlexProject;
import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore.ProjectCreator;
import com.adobe.flexbuilder.project.actionscript.IActionScriptProject;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.internal.FlexProjectSettings;

/**
 * This class is responsible of instantiate a project settings object and returns it as an interface. Because the
 * connector is build against flexbuilder 4.7 components, to not break backward-compatibility, calling a non-existing
 * method on the target object will not throw exceptions. Instead, it will be logged and reported to the user.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class ProjectManager {

  /**
   * The AbstractProxyAdapter is both an adapter to support version prior to 4.7, and a proxy to report a problem when
   * the invoked method is not defined in the target object. This can happen when the interface adds methods as the
   * product version grows, those methods are not present in the former version of the interface.
   * 
   * In all cases, when the system accepts the input data is not as expected, it will produce and output that will
   * identify the problem accurately for the user.
   */
  private static abstract class AbstractProxyAdapter {

    protected Object settings;
    protected IProject project;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Method m = null;
      try {
        // Determines if the method has been defined.
        m = settings.getClass().getMethod(method.getName(), method.getParameterTypes());
        m.setAccessible(true);
      }
      catch (Exception e) {
        String message = "Settings method " + m.getName() + " does not exist.";
        IMarker[] markers = project.findMarkers(IMarker.PROBLEM, false, 0);
        for (IMarker marker : markers) {
          if (marker.getAttribute(IMarker.MESSAGE).equals(message)) {
            return null;
          }
        }
        IMarker marker = project.createMarker(IMarker.PROBLEM);
        marker.setAttribute(IMarker.MESSAGE, message);
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        return null;
      }

      return m.invoke(settings, args);
    }

    public abstract void saveDescription(IProject project, IProgressMonitor monitor) throws CoreException;

  }

  private static class FlexProxyAdapter extends AbstractProxyAdapter implements InvocationHandler {

    public FlexProxyAdapter(IProject project, boolean overrideHTMLWrapperDefault) {
      this.project = project;
//      int major = com.adobe.flexbuilder.project.FlexProjectConstants.AMT_FB4_MAJOR_VERSION;
//      int minor = com.adobe.flexbuilder.project.FlexProjectConstants.AMT_FB4_MINOR_VERSION;
      this.settings = FlexProjectManager.createFlexProjectDescription(
          project.getName(),
          project.getLocation(),
          overrideHTMLWrapperDefault,
          FlexServerType.NO_SERVER /* Since its a Maven project, the server is on another module. */);
    }

    @Override
    public void saveDescription(IProject project, IProgressMonitor monitor) throws CoreException {
      FlexProjectSettings flexProjectSettings = (FlexProjectSettings) settings;
      flexProjectSettings.saveDescription(project, monitor);
    }

  }

  /**
   * Returns a new instance of a Flex project settings.
   * 
   * @param project The project.
   * @param overrideHTMLWrapperDefault
   * @return
   */
  public static IMutableFlexProjectSettings createFlexProjectDescription(IProject project, boolean overrideHTMLWrapperDefault) {
    return (IMutableFlexProjectSettings) Proxy.newProxyInstance(
        ProjectManager.class.getClassLoader(),
        new Class[] {IMutableFlexProjectSettings.class},
        new FlexProxyAdapter(project, overrideHTMLWrapperDefault));
  }

  /**
   * Saves a project settings.
   * 
   * @param project
   * @param settings
   * @param monitor
   * @throws CoreException
   */
  public static void saveDescription(IProject project, IMutableActionScriptProjectSettings settings, IProgressMonitor monitor) throws CoreException {
    AbstractProxyAdapter abstractProxyAdapter = ((AbstractProxyAdapter)Proxy.getInvocationHandler(settings));
    abstractProxyAdapter.saveDescription(project, monitor);
  }

}
