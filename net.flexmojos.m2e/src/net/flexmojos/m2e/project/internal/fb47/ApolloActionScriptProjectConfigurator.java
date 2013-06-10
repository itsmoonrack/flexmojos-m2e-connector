package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.inject.Inject;

public class ApolloActionScriptProjectConfigurator
    extends ActionScriptProjectConfigurator
{

    @Inject ApolloActionScriptProjectConfigurator( final IMavenFlexPlugin plugin,
                                                   final IProject project,
                                                   final IProgressMonitor monitor )
    {
        super( plugin, project, monitor );
    }

}
