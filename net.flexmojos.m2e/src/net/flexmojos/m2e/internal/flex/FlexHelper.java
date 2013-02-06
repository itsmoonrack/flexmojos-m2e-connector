package net.flexmojos.m2e.internal.flex;

public class FlexHelper {

  /**
   * Returns a Flash Builder compatible framework name from flex-sdk-description.xml.
   * 
   * If no version is matching, simply returns "Flex X.Y.Z".
   * 
   * @param fullVersion
   * @return
   */
  public static String getFlexSDKName(String fullVersion) {
    String version = fullVersion.substring(0, 5);
    String name = "Flex ";
    if (version.equals("4.5.1")) {
      name += "4.5.1A";
    }
    else if (version.equals("4.5.0")) {
      name += "4.5A";
    }
    else if (version.equals("4.1.0")) {
      name += "4.1A";
    }
    else if (version.equals("4.0.0")) {
      name += "4.0A";
    }
    else if (version.equals("3.6.0")) {
      name += "3.6A";
    }
    else if (version.equals("3.5.0")) {
      name += "3.5B";
    }
    else if (version.startsWith("3.4")) {
      name += "3.4A";
    }
    else if (version.equals("3.3.0")) {
      name += "3.3A";
    }
    else if (version.equals("3.2.0")) {
      name += "3.2A";
    }
    else if (version.startsWith("3.0")) {
      name += "3A";
    }
    else {
      name += version;
    }
    return name;
  }

}
