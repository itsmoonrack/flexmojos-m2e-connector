package net.flexmojos.m2e.maven.internal;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.AbstractModule;

/**
 * Maven Flex Plug-in abstract components configuration. Depending on the version of the Plug-in, a specialized version
 * of this module is installed to bind the correct dependencies.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public abstract class MavenFlexModule extends AbstractModule
{
    protected final IMavenProjectFacade facade;
    protected final IProgressMonitor monitor;

    protected final String groupId;
    protected final String artifactId;

    public MavenFlexModule( final IMavenProjectFacade facade,
                            final IProgressMonitor monitor,
                            final String groupId,
                            final String artifactId )
    {
        this.facade = facade;
        this.monitor = monitor;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    /**
     * Binds <ll>clazz</ll> to its <ll>implementation</ll> when an <ll>annotation</ll> Mojo is found in the list of
     * <ll>goals</ll> executed.
     *
     * @param clazz
     * @param implementation
     * @param annotation
     * @param goals
     */
    protected <T> void bindMojoExecution( final Class<T> clazz,
                                          final Class<? extends T> implementation,
                                          final Class<? extends Annotation> annotation,
                                          final String... goals )
    {
        List<MojoExecution> executions;

        try
        {
            executions = facade.getMojoExecutions( groupId, artifactId, monitor, goals );
        }
        catch ( final CoreException e )
        {
            executions = new LinkedList<MojoExecution>();
        }

        if ( !executions.isEmpty() )
        {
            bind( MojoExecution.class )
                .annotatedWith( annotation )
                .toInstance( executions.get( 0 ) );
            bind( clazz )
                .to( implementation );
        }
    }

}
