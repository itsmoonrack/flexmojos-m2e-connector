package net.flexmojos.m2e.internal.configurator;

import net.flexmojos.m2e.internal.project.IProjectManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.Inject;

public abstract class AbstractFlexProjectConfigurator extends ActionScriptProjectConfigurator {

  protected AbstractFlexProjectConfigurator() {}

  @Inject
  public AbstractFlexProjectConfigurator(IMavenProjectFacade facade, IProgressMonitor monitor, IProjectManager manager) {
    this.facade = facade;
    this.monitor = monitor;
    this.settings = manager.createFlexProjectDescription(null, false);
  }

}
