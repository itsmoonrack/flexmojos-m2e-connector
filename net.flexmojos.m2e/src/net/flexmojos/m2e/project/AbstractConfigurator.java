package net.flexmojos.m2e.project;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import com.google.inject.Inject;

/**
 * Defines abstraction interface for project configurator.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 */
public abstract class AbstractConfigurator
{
    protected final IMavenFlexPlugin plugin;

    @Inject
    protected AbstractConfigurator( final IMavenFlexPlugin plugin )
    {
        this.plugin = plugin;
    }

    /**
     * Configures the main source folder.
     */
    protected abstract void configureMainSourceFolder();

    /**
     * Configures the source path so the testSourceDirectory, and additional resources locations such as default
     * src/main/resources are added to the class path.
     */
    protected abstract void configureSourcePath();

    /**
     * Configures the target player version, if no version is found, pass the special string "0.0.0" who has the effect
     * of toggling off the version check.
     * 
     * @param configuration
     */
    protected abstract void configureTargetPlayerVersion();

    /**
     * Configures the main application path, if no source file is found, use the default which is inferred from
     * project's name.
     */
    protected abstract void configureMainApplicationPath();

    /**
     * Configures the library path by adding Maven's SWC dependencies of the project.
     */
    protected abstract void configureLibraryPath();

    /**
     * Configures the HTML generation template.
     */
    protected abstract void configureHTMLTemplate();

    /**
     * Configure the additional compiler arguments.
     */
    protected abstract void configureAdditionalCompilerArgs();

    /**
     * Configure the output folder path .
     */
    protected abstract void configureOutputFolderPath();

    /**
     * Configures the project.
     */
    public void configure()
    {
        configureMainSourceFolder();
        configureSourcePath();
        configureOutputFolderPath();
        configureLibraryPath();
        configureHTMLTemplate();
        configureTargetPlayerVersion();
        configureMainApplicationPath();
        configureAdditionalCompilerArgs();
    }

    /**
     * Saves the project description.
     */
    public abstract void saveDescription();

}
