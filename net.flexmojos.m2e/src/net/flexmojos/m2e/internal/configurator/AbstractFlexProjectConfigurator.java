package net.flexmojos.m2e.internal.configurator;

import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.google.inject.Inject;

public abstract class AbstractFlexProjectConfigurator extends ActionScriptProjectConfigurator {

  @Inject
  public AbstractFlexProjectConfigurator(IMavenProjectFacade facade, IMutableFlexProjectSettings settings) {
    super(facade, settings);
  }

}
