package net.flexmojos.m2e.maven.internal.fm7.adapters;

import net.flexmojos.m2e.maven.IGeneratorMojo;
import net.flexmojos.m2e.maven.internal.MavenFlexMojo;
import net.flexmojos.m2e.maven.internal.fm7.GeneratorMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.annotation.Nullable;

import com.google.inject.Inject;

public class Flexmojos7GeneratorMojo extends MavenFlexMojo implements IGeneratorMojo
{

    @Inject Flexmojos7GeneratorMojo( final MavenSession session,
                                     final @GeneratorMojo @Nullable MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }

    @Override
    public IPath getOutputDirectory()
    {
        return new Path( configuration.evaluate( "outputDirectory" ) );
    }

    @Override
    public IPath getBaseOutputDirectory()
    {
        return new Path( configuration.evaluate( "baseOutputDirectory" ) );
    }

}
