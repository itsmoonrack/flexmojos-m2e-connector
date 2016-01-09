package net.flexmojos.m2e.maven;

import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import net.flexmojos.m2e.maven.internal.fm7.Flexmojos7Module;

/**
 * This module is responsible of binding maven common dependencies such as IMavenProjectFacade, MavenSession, and so on.
 *
 * It also installs a sub-module which binds adapters accordingly to the Maven Flex Plug-in version of the project.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class MavenFlexModule extends AbstractModule
{
    protected final IMavenProjectFacade facade;
    protected final IProgressMonitor monitor;
    protected final MavenSession session;

    public MavenFlexModule( final IMavenProjectFacade facade,
                            final IProgressMonitor monitor,
                            final MavenSession session )
    {
        this.facade = facade;
        this.monitor = monitor;
        this.session = session;
    }

    @Provides
    IMavenProjectFacade getMavenProjectFacade()
    {
        return facade;
    }

    @Provides
    MavenSession getMavenSession()
    {
        return session;
    }

    @Provides
    MavenProject getMavenProject(final IMavenProjectFacade facade)
    {
        return facade.getMavenProject();
    }

    @Provides
    Build getBuild(final MavenProject project)
    {
        return project.getBuild();
    }

    @Override
    protected void configure()
    {
        final Map<String, Plugin> plugins = facade.getMavenProject().getBuild().getPluginsAsMap();
        final Plugin plugin;

        if ( plugins.containsKey( "net.flexmojos.oss:flexmojos-maven-plugin" ) )
        {
            plugin = plugins.get( "net.flexmojos.oss:flexmojos-maven-plugin" );
        }
        else if ( plugins.containsKey( "org.sonatype.flexmojos:flexmojos-maven-plugin" ) )
        {
            plugin = plugins.get( "org.sonatype.flexmojos:flexmojos-maven-plugin" );
        }
        else {
            throw new RuntimeException( "Maven Flex Plug-in not found in project build artifacts." );
        }

        switch ( Character.getNumericValue( plugin.getVersion().charAt( 0 ) ) )
        {
            case 7:
            case 6:
            case 5: // TODO: test this is really supported.
            case 4: // TODO: test this is really supported.
                install( new Flexmojos7Module( facade, monitor ) );
                break;

            default:
                throw new RuntimeException( "Maven Flex Plug-in version not supported." );
        }
    }

    public boolean isFlexProject()
    {
        final Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
        // Supports both Adobe and Apache groupId.
        return dependencies.containsKey( "com.adobe.flex.framework:common-framework" )
            || dependencies.containsKey( "org.apache.flex.framework:common-framework" );
    }

    public boolean isApolloProject()
    {
        final Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
        // Supports both Adobe and Apache groupId.
        return dependencies.containsKey( "com.adobe.flex.framework:air-framework" )
            || dependencies.containsKey( "com.adobe.flex.framework.air:air-framework" )
            || dependencies.containsKey( "org.apache.flex.framework.air:air-framework" );
    }

    public String getPackaging()
    {
        return facade.getPackaging();
    }
}
