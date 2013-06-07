package net.flexmojos.m2e.maven.internal.fm6;

import net.flexmojos.m2e.maven.IGeneratorMojo;
import net.flexmojos.m2e.maven.internal.MavenFlexMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class GeneratorMojoAdapter extends MavenFlexMojo implements IGeneratorMojo
{

    @Inject GeneratorMojoAdapter( final MavenSession session, @Named("generate") final MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }

    @Override
    public IPath getOutputDirectory()
    {
        return new Path( evaluate( configuration.getChild( "outputDirectory" ) ) );
    }

    @Override
    public IPath getBaseOutputDirectory()
    {
        return new Path( evaluate( configuration.getChild( "baseOutputDirectory" ) ) );
    }

}
