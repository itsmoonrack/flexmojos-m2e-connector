package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.util.Map;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.maven.internal.fm6.Flexmojos6Adapter;
import net.flexmojos.m2e.project.AbstractConfigurator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Flash Builder abstract components configuration. Depending on the version of FlashBuilder, a specialized version of
 * this module refines the implementation of the abstraction interface AbstractProjectConfigurator.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 */
public abstract class FlashBuilderAbstractModule extends AbstractModule
{
    private final IMavenProjectFacade facade;

    private final IProgressMonitor monitor;

    private final MavenSession session;

    protected FlashBuilderAbstractModule( final IMavenProjectFacade facade,
                                          final IProgressMonitor monitor,
                                          final MavenSession session )
    {
        this.facade = facade;
        this.monitor = monitor;
        this.session = session;
    }

    @Override
    protected void configure()
    {
        final IProject project = facade.getProject();

        // Adds the ActionScript nature.
        addNature( project, "com.adobe.flexbuilder.project.actionscriptnature", monitor );
        // Sets the base project configurator to an ActionScript project configurator. While a project can have multiple
        // natures, a project can not have more than one configurator. The algorithm bellow is based on "the last
        // assignment is the right one" adding natures to the project as the execution flow goes into the branches but
        // overriding configurators to eventually define the project.
        Class<? extends AbstractConfigurator> configurator = getActionScriptProjectConfiguratorClass();

        if ( isApolloProject() )
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

        if ( isFlexProject() )
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

        bind( IProgressMonitor.class ).toInstance( monitor );
        bind( IMavenProjectFacade.class ).toInstance( facade );
        bind( AbstractConfigurator.class ).to( configurator );
        bind( MavenSession.class ).toInstance( session );

        final Plugin flexPlugin = getMavenFlexPlugin();
        final String groupId = flexPlugin.getGroupId();
        final String artifactId = flexPlugin.getArtifactId();
        bind( Plugin.class ).toInstance( flexPlugin );

        try
        {
            // Bind MojoExecution for compile goal.
            final MojoExecution compileMojo = facade.getMojoExecutions( groupId, artifactId, monitor,
                                                                        "compile-swf", "compile-swc").get( 0 );
            bind( MojoExecution.class )
                .annotatedWith( Names.named( "compile" ) )
                .toInstance( compileMojo );
        }
        catch ( final CoreException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        switch ( flexPlugin.getVersion().charAt( 0 ) )
        {
            case 6:
                bind( IMavenFlexPlugin.class ).to( Flexmojos6Adapter.class );
                break;

            default:
                throw new RuntimeException( "Maven Flex Plug-in version is not supported." );
        }
    }

    protected abstract Class<? extends AbstractConfigurator> getActionScriptProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getApolloActionScriptProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getFlexLibraryProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getApolloProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getFlexProjectConfiguratorClass();

    // TODO: Move this in a factory or @Provider. Document, and use a logger.
    /**
     * Return the IMavenFlexPlugin implementation.
     *
     * @return
     * @throws CoreException
     */
    protected Plugin getMavenFlexPlugin()
    {
        String key;
        final Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();

        if ( plugins.containsKey( key = "net.flexmojos.oss:flexmojos-maven-plugin" ) )
        {
        }
        else
        {
            // Informs user the Maven Flex plugin could not be found.
            throw new RuntimeException( "Maven Flex Plug-in not found in project's artifacts." );
        }

        return plugins.get( key );
    }

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isFlexProject()
    {
        final Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
        // Supports both Adobe and Apache groupId.
        return dependencies.containsKey( "com.adobe.flex.framework:common-framework" )
            || dependencies.containsKey( "org.apache.flex.framework:common-framework" );
    }

    private boolean isApolloProject()
    {
        final Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
        // Supports both Adobe and Apache groupId.
        return dependencies.containsKey( "com.adobe.flex.framework.air:air-framework" )
            || dependencies.containsKey( "org.apache.flex.framework.air:air-framework" )
            || dependencies.containsKey( "com.adobe.flex.framework:air-framework" );
    }

}
