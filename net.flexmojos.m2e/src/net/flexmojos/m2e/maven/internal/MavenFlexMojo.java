package net.flexmojos.m2e.maven.internal;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;

public abstract class MavenFlexMojo
{
    protected Configuration configuration;

    protected MavenFlexMojo( final MavenSession session, final MojoExecution mojoExecution )
    {
        this.configuration = new Configuration( session, mojoExecution );
    }
}
