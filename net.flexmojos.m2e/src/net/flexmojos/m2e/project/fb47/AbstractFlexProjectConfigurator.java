package net.flexmojos.m2e.project.fb47;

import net.flexmojos.m2e.flex.FlexFrameworkHelper;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;

public abstract class AbstractFlexProjectConfigurator extends ActionScriptProjectConfigurator {

  protected AbstractFlexProjectConfigurator(final IMavenFlexPlugin plugin) {
    super(plugin);
  }

  /**
   * Configures the Flex SDK name and adds it to the library path of the project.
   * 
   * Must be called before configuring the library path.
   */
  protected void configureFlexSDKName() {
    final String flexVersion = plugin.getFlexFramework().getVersion();
    final String flexSDKName = FlexFrameworkHelper.getFlexSDKName(flexVersion);
    settings.setFlexSDKName(flexSDKName);
  }

}
