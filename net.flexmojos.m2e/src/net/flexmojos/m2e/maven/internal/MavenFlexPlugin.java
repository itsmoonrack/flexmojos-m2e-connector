package net.flexmojos.m2e.maven.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.flexmojos.m2e.maven.ICompilerMojo;
import net.flexmojos.m2e.maven.IGeneratorMojo;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.maven.ISignAirMojo;
import net.flexmojos.m2e.maven.internal.discovery.ServerDiscovery;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.model.Build;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.Inject;

public abstract class MavenFlexPlugin implements IMavenFlexPlugin
{
    protected final IProgressMonitor monitor;
    protected final IMavenProjectFacade facade;

    @Inject protected ICompilerMojo compiler;
    @Inject(optional = true) protected IGeneratorMojo generator;
    @Inject(optional = true) protected ISignAirMojo signAir;

    protected MavenFlexPlugin( final IMavenProjectFacade facade,
                               final IProgressMonitor monitor )
    {
        this.facade = facade;
        this.monitor = monitor;
    }

    protected Build getBuild()
    {
        return facade.getMavenProject().getBuild();
    }

    @Override
    public IPath getMainSourceFolder()
    {
        return facade.getProjectRelativePath( getBuild().getSourceDirectory() );
    }

    @Override
    public Artifact getFlashFramework()
    {
        for ( Artifact artifact : facade.getMavenProject().getArtifacts() )
        {
            if ( isFlashFramework( artifact ) ) return artifact;
        }
        return null;
    }

    @Override
    public Artifact getAirFramework()
    {
        for ( Artifact artifact : facade.getMavenProject().getArtifacts() )
        {
            if ( isAirFramework( artifact ) ) return artifact;
        }
        return null;
    }

    @Override
    public Artifact getFlexFramework()
    {
        for ( Artifact artifact : facade.getMavenProject().getArtifacts() )
        {
            if ( isFlexFramework( artifact ) ) return artifact;
        }
        return null;
    }

    @Override
    public IPath[] getSourcePath()
    {
        final List<IPath> classPath = new ArrayList<IPath>( Arrays.asList( facade.getCompileSourceLocations() ) );

        // Test source directory is treated as a supplementary source path entry so tests can execute in Eclipse.
        final IPath testSourceDirectory = facade.getProjectRelativePath( getBuild().getTestSourceDirectory() );
        if ( testSourceDirectory.toFile().exists() )
        {
            classPath.add( testSourceDirectory );
        }

        return classPath.toArray( new IPath[classPath.size()] );
    }

    @Override
    public String getTargetPlayerVersion()
    {
        return compiler.getTargetPlayerVersion();
    }

    @Override
    public IPath getMainApplicationPath()
    {
        return compiler.getMainApplicationPath();
    }

    @Override
    public boolean hasOutputFolderPath()
    {
        return compiler.hasOutputFolderPath();
    }

    @Override
    public IPath getOutputFolderPath()
    {
        final IPath outputFolderPath = compiler.getOutputFolderPath();
        final IPath outputDirectory = facade.getProjectRelativePath( outputFolderPath.toString() );

        // Checks the outputFolder property has been set or not.
        if ( !compiler.hasOutputFolderPath() )
        {
            // If it does not, triggers the strategy finder.
            final ServerDiscovery discovery = new ServerDiscovery();
            if ( discovery.hasServer( facade.getMavenProject() ) )
            {
                return discovery.getOutputFolderPath();
            }
        }

        // Returns either a relative or an absolute path.
        return outputDirectory != null ? outputDirectory : outputFolderPath;
    }

    @Override
    public IPath getCertificatePath()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Artifact> getDependencies()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPath getLocalesSourcePath()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getLocalesCompiled()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, IPath> getXMLNamespaceManifestPath()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns <tt>true</tt> if this artifact belongs to Flash Framework.
     *
     * @param artifact The artifact to test.
     * @return <tt>true</tt> if this artifact belongs to Flash Framework.
     */
    public static boolean isFlashFramework( final Artifact artifact )
    {
        String artifactKey = ArtifactUtils.versionlessKey(artifact);
        return artifactKey.equals( "com.adobe.flash:framework" )
            || artifactKey.equals( "com.adobe.flash.framework:playerglobal" )
            || artifactKey.equals( "com.adobe.air.framework:playerglobal" )
            || artifactKey.equals( "com.adobe.flex.framework:playerglobal" );
    }

    /**
     * Returns <tt>true</tt> if this artifact belongs to Air Framework.
     *
     * @param artifact The artifact to test.
     * @return <tt>true</tt> if this artifact belongs to Air Framework.
     */
    public static boolean isAirFramework( final Artifact artifact )
    {
        String artifactKey = ArtifactUtils.versionlessKey(artifact);
        return artifactKey.equals( "org.apache.flex.framework:air" )
            || artifactKey.equals( "org.apache.flex.framework.air:airframework" )
            || artifactKey.equals( "com.adobe.air:framework" )
            || artifactKey.equals( "com.adobe.air.framework:airglobal" );
    }

    /**
     * Returns <tt>true</tt> if this artifact belongs to Flex Framework.
     *
     * @param artifact The artifact to test.
     * @return <tt>true</tt> if this artifact belongs to Flex Framework.
     */
    public static boolean isFlexFramework( final Artifact artifact )
    {
        String artifactKey = ArtifactUtils.versionlessKey(artifact);
        return artifactKey.equals( "org.apache.flex.framework:common-framework" )
            || artifactKey.equals( "org.apache.flex.framework:framework" )
            || artifactKey.equals( "com.adobe.flex.framework:common-framework" )
            || artifactKey.equals( "com.adobe.flex.framework:framework" );
    }

}
