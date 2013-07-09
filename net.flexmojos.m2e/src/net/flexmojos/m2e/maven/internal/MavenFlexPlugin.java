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
    public Artifact getFlexFramework()
    {
        final Map<String, Artifact> artifacts = facade.getMavenProject().getArtifactMap();

        // Checks an Apache Flex Framework artifact exists.
        if ( artifacts.containsKey( "org.apache.flex.framework:flex-framework" ) )
            // If it does, return the instance of Apache Flex framework artifact.
            return artifacts.get( "org.apache.flex.framework:flex-framework" );

        // Checks an Adobe Flex Framework artifact exists.
        if ( artifacts.containsKey( "com.adobe.flex.framework:flex-framework" ) )
            // If it does, return the instance of Adobe Flex Framework artifact.
            return artifacts.get( "com.adobe.flex.framework:flex-framework" );

        // TODO: Move the following air-framework in a new method getAirFramework() ?
        if ( artifacts.containsKey( "org.apache.flex.framework.air:air-framework" ) )
            // If it does, return the instance of Apache Flex AIR Framework artifact.
            return artifacts.get( "org.apache.flex.framework.air:air-framework" );

        if ( artifacts.containsKey( "com.adobe.flex.framework.air:air-framework" ) )
            // If it does, return the instance of Adobe Flex AIR Framework artifact.
            return artifacts.get( "com.adobe.flex.framework.air:air-framework" );

        if ( artifacts.containsKey( "com.adobe.flex.framework:air-framework" ) )
            // If it does, return the instance of Adobe Flex AIR Framework artifact.
            return artifacts.get( "com.adobe.flex.framework:air-framework" );

        // Informs user that Flex Framework artifact could not be found.
        throw new RuntimeException( "Flex Framework not found in project's artifacts." );
    }

    @Override
    public IPath[] getSourcePath()
    {
        final List<IPath> classPath = new ArrayList<IPath>( Arrays.asList( facade.getResourceLocations() ) );

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Artifact> getDependencies()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPath getLocalesSourcePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getLocalesCompiled()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, IPath> getXMLNamespaceManifestPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
