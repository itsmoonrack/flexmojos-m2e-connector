package net.flexmojos.m2e.internal;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The AbstractProxyAdapter is both an adapter to support version prior to 4.7, and a proxy to report a problem when
 * the invoked method is not defined in the target object. This can happen when the interface adds methods as the
 * product version grows, those methods are not present in the former version of the interface.
 * 
 * In all cases, when the system accepts the input data is not as expected, it will produce and output that will
 * identify the problem accurately for the user.
 */
public abstract class AbstractProxyAdapter {

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
