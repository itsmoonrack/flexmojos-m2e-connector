package net.flexmojos.m2e.maven.internal.fm6;

import net.flexmojos.m2e.maven.ICompilerMojo;
import net.flexmojos.m2e.maven.internal.MavenFlexMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;

import com.google.inject.Inject;

public class CompilerMojoAdapter extends MavenFlexMojo implements ICompilerMojo
{

    @Inject CompilerMojoAdapter( final MavenSession session, final MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }
}
