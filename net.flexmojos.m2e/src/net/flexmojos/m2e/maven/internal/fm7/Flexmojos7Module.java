package net.flexmojos.m2e.maven.internal.fm7;

import net.flexmojos.m2e.maven.ICompilerMojo;
import net.flexmojos.m2e.maven.IGeneratorMojo;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.maven.ISignAirMojo;
import net.flexmojos.m2e.maven.internal.MavenFlexModule;
import net.flexmojos.m2e.maven.internal.fm7.adapters.Flexmojos7CompilerMojo;
import net.flexmojos.m2e.maven.internal.fm7.adapters.Flexmojos7GeneratorMojo;
import net.flexmojos.m2e.maven.internal.fm7.adapters.Flexmojos7SignAirMojo;
import net.flexmojos.m2e.maven.internal.fm7.adapters.Flexmojos7Plugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

public class Flexmojos7Module extends MavenFlexModule
{
    public Flexmojos7Module( final IMavenProjectFacade facade,
                             final IProgressMonitor monitor )
    {
        super( facade, monitor, "net.flexmojos.oss", "flexmojos-maven-plugin" );
    }

    @Override
    protected void configure()
    {
        bind( IMavenFlexPlugin.class ).to( Flexmojos7Plugin.class );
        bindMojoExecution( ISignAirMojo.class, Flexmojos7SignAirMojo.class, SignAirMojo.class, "sign-air" );
        bindMojoExecution( IGeneratorMojo.class, Flexmojos7GeneratorMojo.class, GeneratorMojo.class, "generate" );
        bindMojoExecution( ICompilerMojo.class, Flexmojos7CompilerMojo.class, CompilerMojo.class, "compile-swc", "compile-swf" );
    }

}
