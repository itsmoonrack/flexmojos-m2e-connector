package net.flexmojos.m2e.maven.internal.fm6.adapters;

import net.flexmojos.m2e.maven.internal.MavenFlexMojo;
import net.flexmojos.m2e.maven.internal.fm6.GeneratorMojo;
import net.flexmojos.m2e.maven.internal.fm6.IGeneratorMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.annotation.Nullable;

import com.google.inject.Inject;

public class Flexmojos6GeneratorMojo extends MavenFlexMojo implements IGeneratorMojo
{

    @Inject Flexmojos6GeneratorMojo( final MavenSession session,
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
