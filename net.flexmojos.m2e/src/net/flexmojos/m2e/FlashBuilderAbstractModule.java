package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.SWF;
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

        // Result of this algorithm. We add all nature directly.
        Class<? extends AbstractConfigurator> configurator;
        
        // Which framework we have is not the point of the project should be what style. Packaging directly affect which
        // project style we use.
        
        // Web project
        boolean isFlash = SWF.equals( facade.getPackaging() );
        // Desktop project
        boolean isApollo = AIR.equals( facade.getPackaging() );
        // Library project
        boolean isLibrary = SWC.equals( facade.getPackaging() );
        
        // Useless
        @SuppressWarnings("unused")
        boolean hasFlashFramework = facade.hasFlashFramework();
        // Useless
        @SuppressWarnings("unused")
        boolean hasAirFramework = facade.hasAirFramework();
        // If we have Flex framework. Project style should be flex project.
        boolean hasFlexFramework = facade.hasFlexFramework();
        
        if ( isFlash )
        {
            if ( hasFlexFramework )
            {
                // Flex Web Style
                addNature( project, "com.adobe.flexbuilder.project.flexnature", monitor );
                addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
                
                configurator = getFlexProjectConfiguratorClass();
            }
            else
            {
                // ActionScript Web Style
                addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
                
                configurator = getActionScriptProjectConfiguratorClass();
            }
        }
        else if ( isApollo )
        {
            if ( hasFlexFramework )
            {
                // Flex Desktop Style
                addNature( project, "com.adobe.flexbuilder.project.flexnature", monitor );
                addNature( project, "com.adobe.flexbuilder.project.apollonature", monitor );
                addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
                
                configurator = getApolloProjectConfiguratorClass();
            }
            else
            {
                // ActionScript Desktop Style
                addNature( project, "com.adobe.flexbuilder.project.apollonature", monitor );
                addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
                
                configurator = getApolloActionScriptProjectConfiguratorClass();
            }
        }
        else if ( isLibrary )
        {
            if ( hasFlexFramework )
            {
                // Flex Library Style
                addNature( project, "com.adobe.flexbuilder.project.flexlibnature", monitor );
                addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
                
                configurator = getFlexLibraryProjectConfiguratorClass();
            }
            else
            {
                // ActionScript Library Style
                addNature( project, "com.adobe.flexbuilder.project.aslibnature", monitor );
                addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
                
                configurator = getFlexLibraryProjectConfiguratorClass();
            }
        }
        else
        {
            throw new AssertionError("Unknown packaging type");
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
