package net.flexmojos.m2e.project.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.google.inject.Inject;

public class FlexProjectConfigurator extends ActionScriptProjectConfigurator {

  @Inject
  public FlexProjectConfigurator(final IMavenProjectFacade facade, final IProgressMonitor monitor, final IMavenFlexPlugin plugin) {
    super(plugin);
    settings = FlexProjectManager.createFlexProjectDescription(
        facade.getProject().getName(),
        facade.getProject().getLocation(),
        false /* FIXME: hard-coded! */,
        FlexServerType.NO_SERVER /* FIXME: hard-coded! */);
  }

}
