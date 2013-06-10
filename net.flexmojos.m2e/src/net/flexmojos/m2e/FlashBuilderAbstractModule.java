package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;
import net.flexmojos.m2e.maven.MavenFlexModule;
import net.flexmojos.m2e.project.AbstractConfigurator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Flash Builder abstract components configuration. Depending on the version of FlashBuilder, a specialized version of
 * this module refines the implementation of the abstraction interface AbstractProjectConfigurator.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 */
public abstract class FlashBuilderAbstractModule extends AbstractModule
{
    private final IProject project;

    private final IProgressMonitor monitor;

    private final MavenFlexModule facade;

    protected FlashBuilderAbstractModule( final ProjectConfigurationRequest request,
                                          final IProgressMonitor monitor )
    {
        this.monitor = monitor;
        this.project = request.getProject();
        this.facade = new MavenFlexModule( request.getMavenProjectFacade(), monitor, request.getMavenSession() );
    }

    @Provides
    IProject getProject(final IMavenProjectFacade facade)
    {
        return project;
    }

    @Provides
    IProgressMonitor getProgressMonitor()
    {
        return monitor;
    }

    @Override
    protected void configure()
    {
        // Installs the facade to maven project, by configuring a concrete module depending on the version of the Maven
        // Flex Plug-in available.
        install( facade );

        // Adds the ActionScript nature.
        addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
        // Sets the base project configurator to an ActionScript project configurator. While a project can have multiple
        // natures, a project can not have more than one configurator. The algorithm bellow is based on "the last
        // assignment is the right one" adding natures to the project as the execution flow goes into the branches but
        // overriding configurators to eventually define the project.
        Class<? extends AbstractConfigurator> configurator = getActionScriptProjectConfiguratorClass();

        if ( facade.isApolloProject() )
        {
            // An Apollo project exists in two flavors: ApolloActionScriptProject, and ApolloProject. While the former
            // directly extends from ActionScriptProject, the later inherits from FlexProject, so it is perfectly
            // possible for an Apollo project to have a Flex nature as well.
            addNature( project, "com.adobe.flexbuilder.project.apollonature", monitor );
            // The configurator will replace the ActionScript project configurator initially set by an
            // ApolloActionScript project configurator. Later in the execution flow, in the case a
            // project have the Flex nature as well, the configurator will be replaced by a "pure" Apollo project
            // configurator.
            configurator = getApolloActionScriptProjectConfiguratorClass();
        }

        if ( facade.isFlexProject() )
        {
            // Depending on the packaging, a Flex project can be a FlexLibraryProject (SWC), a FlexProject (SWF) or an
            // ApolloProject (AIR).
            if ( SWC.equals( facade.getPackaging() ) )
            {
                addNature( project, "com.adobe.flexbuilder.project.flexlibnature", monitor );
                configurator = getFlexLibraryProjectConfiguratorClass();
                // End of algorithm.
            }
            else
            {
                // An AIR and SWF packaging indicates respectively an ApolloProject and a FlexProject, in both case the
                // Flex nature is added to the project.
                addNature( project, "com.adobe.flexbuilder.project.flexnature", monitor );
                if ( AIR.equals( facade.getPackaging() ) )
                {
                    configurator = getApolloProjectConfiguratorClass();
                    // End of algorithm.
                }
                else
                {
                    configurator = getFlexProjectConfiguratorClass();
                    // End of algorithm.
                }
            }
        }
        else if ( SWC.equals( facade.getPackaging() ) )
        {
            // In the case there is no declared Flex dependencies, and the packaging is SWC, its an ActionScriptProject
            // with an aslib nature.
            addNature( project, "com.adobe.flexbuilder.project.aslibnature", monitor );
            // End of algorithm.
        }

        bind( AbstractConfigurator.class ).to( configurator );
    }

    protected abstract Class<? extends AbstractConfigurator> getActionScriptProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getApolloActionScriptProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getFlexLibraryProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getApolloProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getFlexProjectConfiguratorClass();

    /**
     * Short-hand method for wrapping an "addNature" operation.
     *
     * @param project
     * @param natureId
     * @param monitor
     */
    private void addNature( final IProject project, final String natureId, final IProgressMonitor monitor )
    {
        try
        {
            org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator.addNature( project, natureId, monitor );
        }
        catch ( final CoreException e )
        {
            throw new RuntimeException( e );
        }
    }
}
