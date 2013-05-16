package net.flexmojos.m2e.project.fb47;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.flexmojos.m2e.flex.FlexCompilerArguments;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.project.AbstractConfigurator;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.ClassPathEntryFactory;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.common.CrossDomainRslEntry;
import com.adobe.flexbuilder.util.FlashPlayerVersion;
import com.google.inject.Inject;

public class ActionScriptProjectConfigurator
    extends AbstractConfigurator
{

    protected IProject project;

    protected IProgressMonitor monitor;

    protected IMutableActionScriptProjectSettings settings;

    protected ActionScriptProjectConfigurator( final IMavenFlexPlugin plugin )
    {
        super( plugin );
    }

    @Inject
    public ActionScriptProjectConfigurator( final IMavenProjectFacade facade, final IProgressMonitor monitor,
                                            final IMavenFlexPlugin plugin )
    {
        this( plugin );
        this.monitor = monitor;
        project = facade.getProject();
        settings = ActionScriptCore.createProjectDescription( project.getName(), project.getLocation(), false
        /* FIXME : hard - coded ! */);
    }

    @Override
    public void saveDescription()
    {
    }

    @Override
    protected void configureMainSourceFolder()
    {
        settings.setMainSourceFolder( plugin.getMainSourceFolder() );
    }

    @Override
    protected void configureHTMLTemplate()
    {
        IFolder template = project.getFolder( "html-template" );
        if ( template.exists() )
        {
            settings.setHTMLExpressInstall( true );
            settings.setHTMLPlayerVersionCheck( true );
            settings.setGenerateHTMLWrappers( true );
            settings.setEnableHistoryManagement( true );
        }
        else
        {
            settings.setHTMLExpressInstall( false );
            settings.setHTMLPlayerVersionCheck( false );
            settings.setGenerateHTMLWrappers( false );
            settings.setEnableHistoryManagement( false );
        }
    }

    @Override
    protected void configureSourcePath()
    {
        final IPath[] paths = plugin.getSourcePath();
        final IClassPathEntry[] classPath = new IClassPathEntry[paths.length];

        for ( int i = 0; i < paths.length; i++ )
        {
            // Converts IPath to IClassPathEntry.
            classPath[i] = ClassPathEntryFactory.newEntry( paths[i].toString(), settings );

        }

        settings.setSourcePath( classPath );
    }

    @Override
    protected void configureTargetPlayerVersion()
    {
        final String playerBinary = plugin.getTargetPlayerVersion();
        final FlashPlayerVersion version = new FlashPlayerVersion( playerBinary == null ? "0.0.0" : playerBinary );
        settings.setTargetPlayerVersion( version );
    }

    @Override
    protected void configureMainApplicationPath()
    {
        final IPath mainApplicationPath = plugin.getMainApplicationPath();
        if ( mainApplicationPath != null )
        {
            settings.setApplicationPaths( new IPath[] { mainApplicationPath } );
            settings.setMainApplicationPath( mainApplicationPath );
        }
    }

    @Override
    protected void configureLibraryPath()
    {
        final Map<String, Artifact> dependencies = plugin.getDependencies();
        final Map<String, IClassPathEntry> classPath = new LinkedHashMap<String, IClassPathEntry>();
        for ( final IClassPathEntry entry : settings.getLibraryPath() )
        {
            // Copy previous library path that exists in project's dependencies.
            if ( dependencies.containsKey( entry.getValue() ) )
                classPath.put( entry.getValue(), entry );

            // Copy Flex dependency.
            else if ( entry instanceof ClassPathEntryFactory.FlexSDKClasspathEntry )
                classPath.put( "flex-framework", entry );
        }

        for ( final Artifact artifact : dependencies.values() )
        {
            // Copy dependencies to new class path.
            String path = artifact.getFile().getAbsolutePath();
            if ( !path.contains( ".swc" ) && !path.contains( ".swf" ) )
            {
                path = artifact.getFile() + "/" + artifact.getArtifactId() + "." + artifact.getType();
            }
            final String scope = artifact.getScope();
            final IClassPathEntry entry =
                ClassPathEntryFactory.newEntry( IClassPathEntry.KIND_LIBRARY_FILE, path, settings );

            if ( scope.equals( "rsl" ) && ( project instanceof FlexProjectConfigurator ) )
            {
                entry.setLinkType( IClassPathEntry.LINK_TYPE_CROSS_DOMAIN_RSL );
                entry.setCrossDomainRsls( new CrossDomainRslEntry[] { new CrossDomainRslEntry( artifact.getArtifactId()
                    + ".swf", "", true ) } );
            }

            if ( !scope.equals( "test" ) )
                // Adds entry to class path.
                classPath.put( path, entry );
        }
        settings.setLibraryPath( classPath.values().toArray( new IClassPathEntry[classPath.size()] ) );
    }

    @Override
    protected void configureAdditionalCompilerArgs()
    {
        final FlexCompilerArguments arguments = new FlexCompilerArguments();

        // Sets source-path argument.
        final List<String> pathElements = new LinkedList<String>();
        if ( plugin.getLocalesSourcePath() != null )
        {
            pathElements.add( plugin.getLocalesSourcePath().toString() );
        }
        arguments.setSourcePath( pathElements );

        // Sets locale argument.
        final List<String> locales = new ArrayList<String>();
        locales.addAll( Arrays.asList( plugin.getLocalesCompiled() ) );
        arguments.setLocalesCompiled( locales );

        settings.setAdditionalCompilerArgs( arguments.toString() );
    }

    @Override
    protected void configureOutputFolderPath()
    {
        settings.setOutputFolder( plugin.getOutputFolderPath() );
    }

}
