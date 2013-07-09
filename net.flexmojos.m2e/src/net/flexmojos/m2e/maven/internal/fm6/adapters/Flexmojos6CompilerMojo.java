package net.flexmojos.m2e.maven.internal.fm6.adapters;

import net.flexmojos.m2e.maven.ICompilerMojo;
import net.flexmojos.m2e.maven.internal.MavenFlexMojo;
import net.flexmojos.m2e.maven.internal.fm6.CompilerMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.inject.Inject;

public class Flexmojos6CompilerMojo extends MavenFlexMojo implements ICompilerMojo
{

    @Inject Flexmojos6CompilerMojo( final MavenSession session,
                                    final @CompilerMojo MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }

    @Override
    public String getTargetPlayerVersion()
    {
        return configuration.evaluate( "targetPlayer" );
    }

    @Override
    public IPath getMainApplicationPath()
    {
        final String sourceFile = configuration.evaluate( "sourceFile" );
        return sourceFile == null ? null : new Path( sourceFile );
    }

    public boolean hasOutputFolderPath()
    {
        return configuration.exists( "outputDirectory" );
    }

    @Override
    public IPath getOutputFolderPath()
    {
        return new Path( configuration.evaluate( "outputDirectory" ) );
    }
}
