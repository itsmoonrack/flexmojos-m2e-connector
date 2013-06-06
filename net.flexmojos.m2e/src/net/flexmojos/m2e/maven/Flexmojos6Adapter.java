package net.flexmojos.m2e.maven;

import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.XMLNamespaceManifestPath;
import com.google.inject.Inject;

/**
 * Implementation of the Flexmojos 6.x plugin.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 */
public class Flexmojos6Adapter
    implements IMavenFlexPlugin
{
    protected IMavenProjectFacade facade;

    protected IProgressMonitor monitor;

    protected ExpressionEvaluator evaluator;

    protected Xpp3Dom configuration;

    @Inject
    public Flexmojos6Adapter( final IMavenProjectFacade facade, final IProgressMonitor monitor,
                              final MavenSession session, final MojoExecution mojo )
    {
        this.facade = facade;
        this.monitor = monitor;
        evaluator = new PluginParameterExpressionEvaluator( session, mojo );
        configuration = mojo.getConfiguration();
    }

    /**
     * Short-hand method for evaluating a configuration value.
     * 
     * @return
     */
    protected String evaluate( final Xpp3Dom conf )
    {
        try
        {
            if ( conf.getValue() != null )
                return (String) evaluator.evaluate( conf.getValue() );
            else
                return (String) evaluator.evaluate( conf.getAttribute( "default-value" ) );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    @Override
    public IPath getMainSourceFolder()
    {
        final Build build = facade.getMavenProject().getBuild();
        return facade.getProjectRelativePath( build.getSourceDirectory() );
    }

    @Override
    public Artifact getFlexFramework()
    {
        final Map<String, Artifact> artifacts = facade.getMavenProject().getArtifactMap();

        // Checks an Apache Flex Framework artifact exists.
        if ( artifacts.containsKey( "org.apache.flex.framework:flex-framework" ) )
            // If it does, return the instance of Apache Flex framework
            // artifact.
            return artifacts.get( "org.apache.flex.framework:flex-framework" );

        // Checks an Adobe Flex Framework artifact exists.
        if ( artifacts.containsKey( "com.adobe.flex.framework:flex-framework" ) )
            // If it does, return the instance of Adobe Flex Framework artifact.
            return artifacts.get( "com.adobe.flex.framework:flex-framework" );

        if ( artifacts.containsKey( "org.apache.flex.framework.air:air-framework" ) )
            // If it does, return the instance of Adobe Flex AIR Framework artifact.
            return artifacts.get( "org.apache.flex.framework.air:air-framework" );

        if ( artifacts.containsKey( "com.adobe.flex.framework.air:air-framework" ) )
            // If it does, return the instance of Adobe Flex AIR Framework artifact.
            return artifacts.get( "com.adobe.flex.framework.air:air-framework" );

        if ( artifacts.containsKey( "com.adobe.flex.framework:air-framework" ) )
            // If it does, return the instance of Adobe Flex AIR Framework artifact.
            return artifacts.get( "com.adobe.flex.framework:air-framework" );

        // Inform user that Flex Framework artifact could not be found.
        throw new RuntimeException( "Flex Framework not found in project's artifacts." );
    }

    @Override
    public IPath[] getSourcePath()
    {
        final List<IPath> classPath = new ArrayList<IPath>( Arrays.asList( facade.getResourceLocations() ) );
        // The test source directory is treated as a supplementary source path
        // entry.
        final Build build = facade.getMavenProject().getBuild();
        final IPath testSourceDirectory = facade.getProjectRelativePath( build.getTestSourceDirectory() );
        if ( testSourceDirectory.toFile().exists() )
        {
            classPath.add( testSourceDirectory );
        }

        return classPath.toArray( new IPath[classPath.size()] );
    }

    @Override
    public String getTargetPlayerVersion()
    {
        return evaluate( configuration.getChild( "targetPlayer" ) );
    }

    @Override
    public IPath getMainApplicationPath()
    {
        final String sourceFile = evaluate( configuration.getChild( "sourceFile" ) );
        return sourceFile == null ? null : new Path( sourceFile );
    }

    @Override
    public Map<String, Artifact> getDependencies()
    {
        final Map<String, Artifact> dependencies = new LinkedHashMap<String, Artifact>();

        for ( final Artifact artifact : facade.getMavenProject().getArtifacts() )
        {
            // Only manage SWC type dependencies.
            if ( SWC.equals( artifact.getType() ) && !isAirFramework( artifact ) && !isFlashFramework( artifact )
                && !isFlexFramework( artifact ) )
            {
                dependencies.put( artifact.getFile().getAbsolutePath(), artifact );
            }
        }

        return dependencies;
    }

    @Override
    public IPath getLocalesSourcePath()
    {
        final String localesSourcePath = evaluate( configuration.getChild( "localesSourcePath" ) );
        final IPath path = facade.getProjectRelativePath( localesSourcePath );
        if ( path.toFile().exists() )
            return facade.getProjectRelativePath( localesSourcePath );
        else
            return null;
    }

    @Override
    public String[] getLocalesCompiled()
    {
        final Xpp3Dom localesCompiled = configuration.getChild( "localesCompiled" );
        if ( localesCompiled != null )
        {
            final String[] locales = new String[localesCompiled.getChildCount()];

            for ( int i = 0; i < localesCompiled.getChildCount(); i++ )
            {
                locales[i] = localesCompiled.getChild( i ).getValue();
            }

            return locales;
        }
        else
        {
            return new String[0];
        }
    }

    @Override
    public XMLNamespaceManifestPath[] getXMLNamespaceManifestPath()
    {
        final Xpp3Dom namespacesTag = configuration.getChild( "namespaces" );
        if ( namespacesTag != null )
        {
            final XMLNamespaceManifestPath[] namespaces = new XMLNamespaceManifestPath[namespacesTag.getChildCount()];
            for ( int i = 0; i < namespacesTag.getChildCount(); i++ )
            {
                namespaces[i] =
                    new XMLNamespaceManifestPath(
                                                  namespacesTag.getChild( i ).getChild( "uri" ).getValue(),
                                                  facade.getFullPath( new File(
                                                                                namespacesTag.getChild( i ).getChild( "manifest" ).getValue() ) ) );
            }
            return namespaces;
        }
        else
        {
            return new XMLNamespaceManifestPath[0];
        }
    }

    @Override
    public IPath getOutputFolderPath()
    {
        return facade.getProjectRelativePath( evaluate( configuration.getChild( "outputDirectory" ) ) );
    }

    /**
     * Returns <tt>true</tt> if this artifact belongs to Air Framework.
     * 
     * @param artifact The artifact to test.
     * @return <tt>true</tt> if this artifact belongs to Air Framework.
     */
    protected boolean isAirFramework( final Artifact artifact )
    {
        return artifact.getGroupId().startsWith( "com.adobe.air.framework" );
    }

    /**
     * Returns <tt>true</tt> if this artifact belongs to Flash Framework.
     * 
     * @param artifact The artifact to test.
     * @return <tt>true</tt> if this artifact belongs to Flash Framework.
     */
    protected boolean isFlashFramework( final Artifact artifact )
    {
        return artifact.getGroupId().startsWith( "com.adobe.flash.framework" );
    }

    /**
     * Returns <tt>true</tt> if this artifact belongs to Flex Framework.
     * 
     * @param artifact The artifact to test.
     * @return <tt>true</tt> if this artifact belongs to Flex Framework.
     */
    protected boolean isFlexFramework( final Artifact artifact )
    {
        return artifact.getGroupId().startsWith( "com.adobe.flex.framework" )
            || artifact.getGroupId().startsWith( "org.apache.flex.framework" );
    }

    @Override
    public IPath getCertificatePath()
    {
        final Xpp3Dom airConfig =
            facade.getMavenProject().getGoalConfiguration( "net.flexmojos.oss", "flexmojos-maven-plugin",
                                                           "default-sign-air", "sign-air" );

        final Xpp3Dom keystoreTag = airConfig.getChild( "keystore" );

        if ( keystoreTag != null )
            return facade.getProjectRelativePath( evaluate( keystoreTag ) );
        else
            return null;
    }

}
