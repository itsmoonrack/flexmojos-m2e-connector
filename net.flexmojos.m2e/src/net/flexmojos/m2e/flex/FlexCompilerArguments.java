package net.flexmojos.m2e.flex;

import java.util.LinkedList;
import java.util.List;

/**
 * Mxmlc compiler specifications.
 * 
 * @see http://help.adobe.com/en_US/flex/using/WS2db454920e96a9e51e63e3d11c0bf69084-7a92.html
 */
public class FlexCompilerArguments {

  List<String> arguments = new LinkedList<String>();

  public FlexCompilerArguments() {

  }

  /**
   * Adds directories or files to the source path. The Flex compiler searches directories in the source path for MXML,
   * AS, or CSS source files that are used in your applications and includes those that are required at compile time.
   * 
   * @param resourceBundlePath
   */
  public void setSourcePath(List<String> pathElements) {
    if (pathElements.size() != 0) {
      arguments.add("-source-path=" + implode(" ", pathElements));
    }
  }

  /**
   * Specifies one or more locales to be compiled into the SWF file. If you do not specify a locale, then the compiler
   * uses the default locale from the flex-config.xml file. The default value is en_US.
   * 
   * @param localesCompiled
   */
  public void setLocalesCompiled(List<String> locales) {
    if (locales.size() != 0) {
      arguments.add("-locale=" + implode(",", locales));
    }
  }

  /**
   * Join array elements with a string.
   * 
   * @param glue
   * @param pieces
   */
  private String implode(String glue, List<String> pieces) {
    StringBuilder builder = new StringBuilder();

    builder.append(pieces.remove(0));
    for (String piece : pieces) {
      builder.append(glue + piece);
    }

    return builder.toString();
  }

  public String toString() {
    return arguments.size() == 0 ? "" : implode(" ", arguments);
  }
}
