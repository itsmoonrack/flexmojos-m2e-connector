package net.flexmojos.m2e.internal;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.adobe.flexbuilder.util.FlashPlayerVersion;

public abstract class AbstractFlexProjectConfigurator extends ActionScriptProjectConfigurator {

  Xpp3Dom configuration;

  public void configureTargetPlayerVersion(IMutableFlexProjectSettings settings) {
    Xpp3Dom targetPlayer = configuration.getChild("targetPlayer");
    if (targetPlayer != null) {
      try {
        String formattedVersionString = targetPlayer.getValue();
        settings.setTargetPlayerVersion(new FlashPlayerVersion(formattedVersionString));
      }
      catch (Exception e) {
        // TODO test if targetPlayer is supported in Flash 4.0 and remove if yes.
        addMarker("test marker");
      }
    }
  }

}
