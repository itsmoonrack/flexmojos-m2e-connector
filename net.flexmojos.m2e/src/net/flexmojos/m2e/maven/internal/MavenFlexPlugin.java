package net.flexmojos.m2e.maven.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

public abstract class MavenFlexPlugin implements IMavenFlexPlugin
{
    protected final IMavenProjectFacade facade;

    protected final IProgressMonitor monitor;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPath getMainApplicationPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPath getOutputFolderPath()
    {
        // TODO Auto-generated method stub
        return null;
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
