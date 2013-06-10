package net.flexmojos.m2e.maven.internal.fm6;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.maven.internal.MavenFlexModule;
import net.flexmojos.m2e.maven.internal.fm6.adapters.Flexmojos6CompilerMojo;
import net.flexmojos.m2e.maven.internal.fm6.adapters.Flexmojos6GeneratorMojo;
import net.flexmojos.m2e.maven.internal.fm6.adapters.Flexmojos6Plugin;
import net.flexmojos.m2e.maven.internal.fm6.adapters.Flexmojos6SignAirMojo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

public class Flexmojos6Module extends MavenFlexModule
{
    public Flexmojos6Module( final IMavenProjectFacade facade,
                             final IProgressMonitor monitor )
    {
        super( facade, monitor, "net.flexmojos.oss", "flexmojos-maven-plugin" );
    }

    @Override
    protected void configure()
    {
        bind( IMavenFlexPlugin.class ).to( Flexmojos6Plugin.class );
        bindMojoExecution( ISignAirMojo.class, Flexmojos6SignAirMojo.class, SignAirMojo.class, "sign-air" );
        bindMojoExecution( IGeneratorMojo.class, Flexmojos6GeneratorMojo.class, GeneratorMojo.class, "generate" );
        bindMojoExecution( ICompilerMojo.class, Flexmojos6CompilerMojo.class, CompilerMojo.class, "compile-swc", "compile-swf" );
    }

}
