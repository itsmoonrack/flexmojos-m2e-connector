package net.flexmojos.m2e.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.core.resources.IProject;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;


public class SettingsProxyFactory {

  private static class ProxyAdapter implements InvocationHandler {
    
    private final IMutableFlexProjectSettings settings;
    
    public ProxyAdapter(IProject project) {
      settings = FlexProjectManager.createFlexProjectDescription(
          project.getName(),
          project.getLocation(),
          false /* FIXME: overrideHTMLWrapperDefault */,
          FlexServerType.NO_SERVER /* Since its a Maven project, the server is on another module. */);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Method m = null;
      try {
        // Determines if the method has been defined.
        m = ProxyAdapter.this.getClass().getMethod(method.getName(), method.getParameterTypes());
        m.setAccessible(true);
      }
      catch (Exception e) {
        // Do nothing for now but can report a marker in the pom.xml to tell the programmer that some settings
        // was not set.
        return null;
      }

      return m.invoke(settings, args);
    }

  }

  public static IMutableFlexProjectSettings getFlexProjectSettings(IProject project) {
    return (IMutableFlexProjectSettings) Proxy.newProxyInstance(
        ProxyAdapter.class.getClassLoader(),
        new Class[] {IMutableFlexProjectSettings.class},
        new ProxyAdapter(project));
  }

}
