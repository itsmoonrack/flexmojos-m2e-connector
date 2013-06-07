package net.flexmojos.m2e.maven.internal.fm6;

import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.maven.internal.MavenFlexPlugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.Inject;

/**
 * Implementation of the Flexmojos 6.x plugin.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 * @author Sebastien Pinel
 */
public abstract class Flexmojos6Adapter extends MavenFlexPlugin
implements IMavenFlexPlugin
{
    @Inject Flexmojos6Adapter( final IMavenProjectFacade facade,
                               final IProgressMonitor monitor,
                               final MavenSession session,
                               final Plugin plugin )
    {
        super( facade, monitor, session, plugin );
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

        // TODO: Move the following air-framework in a new method getAirFramework() ?
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
        return configuration.evaluate( "targetPlayer" );
    }

    @Override
    public IPath getMainApplicationPath()
    {
        final String sourceFile = configuration.evaluate( "sourceFile" );
        return sourceFile == null ? null : new Path( sourceFile );
    }

    @Override
    public Map<String, Artifact> getDependencies()
    {
        final Map<String, Artifact> dependencies = new LinkedHashMap<String, Artifact>();

        for ( final Artifact artifact : facade.getMavenProject().getArtifacts() )
        {
            // Only manage SWC type dependencies.
            if ( SWC.equals( artifact.getType() ) && !isAirFramework( artifact ) && !isFlashFramework( artifact ) && !isFlexFramework( artifact ) )
            {
                dependencies.put( artifact.getFile().getAbsolutePath(), artifact );
            }
        }

        return dependencies;
    }

    @Override
    public IPath getLocalesSourcePath()
    {
        final String localesSourcePath = configuration.evaluate( "localesSourcePath" );
        final IPath path = facade.getProjectRelativePath( localesSourcePath );
        // Checks the base path (without the placeholder {locale} exists).
        return facade.getProject().exists( path.removeLastSegments( 1 ) ) ? path : null;
    }

    @Override
    public String[] getLocalesCompiled()
    {
        final Xpp3Dom localesCompiled = null; //configuration.getChild( "localesCompiled" );
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
    public Map<String, IPath> getXMLNamespaceManifestPath()
    {
        final Xpp3Dom namespacesTag = null;// configuration.getChild( "namespaces" );
        final Map<String, IPath> namespaces = new LinkedHashMap<String, IPath>();

        if ( namespacesTag != null )
        {
            for ( final Xpp3Dom namespace : namespacesTag.getChildren() )
            {
                final String key = namespace.getChild( "uri" ).getValue();
                final IPath value = facade.getFullPath(new File(namespace.getChild( "manifest" ).getValue()));
                namespaces.put( key, value );
            }
        }

        return namespaces;
    }

    @Override
    public IPath getCertificatePath()
    {
        final Xpp3Dom airConfig =
                        facade.getMavenProject().getGoalConfiguration( "net.flexmojos.oss", "flexmojos-maven-plugin",
                                                                       "default-sign-air", "sign-air" );

        final Xpp3Dom keystoreTag = airConfig.getChild( "keystore" );

        if ( keystoreTag != null )
            return facade.getProjectRelativePath( null /* evaluate( keystoreTag ) */ );
        else
            return null;
    }

    @Override
    public IPath getOutputFolderPath()
    {
        final Xpp3Dom outputDirectory = null; //configuration.getChild( "outputDirectory" );

        // Checks outputDirectory has been set, e.g. if not returning default value.
        if (outputDirectory.getValue().equals( "${project.build.outputDirectory}" ))
        {
            return facade.getProjectRelativePath( null /*evaluate( outputDirectory )*/ );
        }
        else {
            return new Path(null /*evaluate( outputDirectory )*/);
        }
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

}
