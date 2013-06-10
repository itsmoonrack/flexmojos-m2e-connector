package net.flexmojos.m2e.maven.internal.fm6.adapters;

import net.flexmojos.m2e.maven.internal.MavenFlexMojo;
import net.flexmojos.m2e.maven.internal.fm6.ISignAirMojo;
import net.flexmojos.m2e.maven.internal.fm6.SignAirMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.jdt.annotation.Nullable;

import com.google.inject.Inject;

public class Flexmojos6SignAirMojo extends MavenFlexMojo implements ISignAirMojo
{

    @Inject Flexmojos6SignAirMojo( final MavenSession session,
                                   final @SignAirMojo @Nullable MojoExecution mojoExecution )
    {
        super( session, mojoExecution );
    }

}
