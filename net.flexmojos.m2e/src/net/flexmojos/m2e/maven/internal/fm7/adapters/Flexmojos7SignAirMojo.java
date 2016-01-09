package net.flexmojos.m2e.maven.internal.fm7.adapters;

import net.flexmojos.m2e.maven.ISignAirMojo;
import net.flexmojos.m2e.maven.internal.MavenFlexMojo;
import net.flexmojos.m2e.maven.internal.fm7.SignAirMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.jdt.annotation.Nullable;

import com.google.inject.Inject;

public class Flexmojos7SignAirMojo extends MavenFlexMojo implements ISignAirMojo
{

    @Inject Flexmojos7SignAirMojo( final MavenSession session,
                                   final @SignAirMojo @Nullable MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }

}
