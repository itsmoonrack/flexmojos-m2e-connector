package net.flexmojos.m2e.maven.internal.fm6.adapters;

import net.flexmojos.m2e.maven.internal.MavenFlexMojo;
import net.flexmojos.m2e.maven.internal.fm6.CompilerMojo;
import net.flexmojos.m2e.maven.internal.fm6.ICompilerMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;

import com.google.inject.Inject;

public class Flexmojos6CompilerMojo extends MavenFlexMojo implements ICompilerMojo
{

    @Inject Flexmojos6CompilerMojo( final MavenSession session,
                                    final @CompilerMojo MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }
}
