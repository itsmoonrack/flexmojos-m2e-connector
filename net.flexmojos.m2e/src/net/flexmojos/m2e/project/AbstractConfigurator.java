package net.flexmojos.m2e.project;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import com.google.inject.Inject;

/**
 * Defines abstraction interface for project configurator.
 * 
 * The Project Configurator is responsible of configuring an eclipse project, by creating and maintaining configuration
 * files.
 * 
 * The Abstract configurator collaborates with an instance of Implementor reference. The pattern used is a bridge so
 * the two classes (Abstraction and Implementor) can vary independently in order to cover the product and version space
 * in a way that it is easy to maintain (Flash Builder 4.0, 4.5, 4.6 and 4.7 with Flexmojos 4, 5, 6 and MavenFlexPlugin).
 * 
 * Classes refining this abstraction should have an inheritance tree model that reflect Adobe project internal
 * component model, thus they can rely on classes such as ActionScriptProjectSetting for instance.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public abstract class AbstractConfigurator {

  protected final IMavenFlexPlugin plugin;

  @Inject
  protected AbstractConfigurator(final IMavenFlexPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Configures the main source folder.
   */
  protected abstract void configureMainSourceFolder();

  /**
   * Configures the source path so the testSourceDirectory, and additional
   * resources locations such as default src/main/resources are added to the
   * class path.
   */
  protected abstract void configureSourcePath();

  /**
   * Configures the target player version, if no version is found, pass the
   * special string "0.0.0" who has the effect of toggling off the version
   * check.
   * 
   * @param configuration
   */
  protected abstract void configureTargetPlayerVersion();

  /**
   * Configures the main application path, if no source file is found, use the
   * default which is inferred from project's name.
   */
  protected abstract void configureMainApplicationPath();

  /**
   * Configures the library path by adding Maven's SWC dependencies of the project.
   */
  protected abstract void configureLibraryPath();

  /**
   * Configure the additional compiler arguments.
   */
  protected abstract void configureAdditionalCompilerArgs();

  /**
   * Configures the project.
   */
  public void configure() {
    configureMainSourceFolder();
    configureSourcePath();
    configureLibraryPath();

    configureTargetPlayerVersion();
    configureMainApplicationPath();
    configureAdditionalCompilerArgs();
  }

  /**
   * Saves the project description.
   */
  public abstract void saveDescription();

}
