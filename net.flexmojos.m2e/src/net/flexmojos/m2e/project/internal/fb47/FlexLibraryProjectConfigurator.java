package net.flexmojos.m2e.project.internal.fb47;

import java.util.Map;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.IFlexLibraryProject;
import com.adobe.flexbuilder.project.XMLNamespaceManifestPath;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.internal.FlexLibraryProjectSettings;
import com.google.inject.Inject;

public class FlexLibraryProjectConfigurator
extends AbstractFlexProjectConfigurator
{

    @Inject FlexLibraryProjectConfigurator( final IMavenFlexPlugin plugin,
                                            final IProject project,
                                            final IProgressMonitor monitor )
    {
        super( plugin, project, monitor );
    }

    @Override
    protected void createConfiguration()
    {
        final IFlexLibraryProject flexProject = (IFlexLibraryProject) ActionScriptCore.getProject( project );
        // Checks if project already exists.
        if ( flexProject != null )
        {
            // If it does, reuse the settings.
            settings = flexProject.getFlexLibraryProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.
            settings = FlexProjectManager
                            .createFlexLibraryProjectDescription( project.getName(),
                                                                  project.getLocation(),
                                                                  false /* FIXME: hard-coded ! */);
        }
    }

    @Override
    protected void saveDescription()
    {
        final FlexLibraryProjectSettings flexProjectSettings = (FlexLibraryProjectSettings) settings;
        flexProjectSettings.saveDescription( project, monitor );
    }

    @Override
    protected void configureSDKUse()
    {
        settings.setUseFlashSDK( plugin.getFlexFramework() == null );
        settings.setUseAIRConfig( plugin.getAirFramework() != null );
    }

    @Override
    protected void configureLibraryPath()
    {
        if ( plugin.getFlexFramework() != null )
        {
            super.configureFlexSDKName();
        }
        super.configureLibraryPath();
    }

    protected void configureManifest()
    {
        if ( plugin.getFlexFramework() != null )
        {
            final Map<String, IPath> namespaces = plugin.getXMLNamespaceManifestPath();
            final XMLNamespaceManifestPath[] paths = new XMLNamespaceManifestPath[namespaces.size()];
            int iterator = 0;
    
            for (final Map.Entry<String, IPath> namespace : namespaces.entrySet())
            {
                // Converts <String, IPath> to XMLNamespaceManifestPath.
                paths[iterator++] = new XMLNamespaceManifestPath( namespace.getKey(), namespace.getValue() );
            }
    
            ((FlexLibraryProjectSettings) settings).setManifestPaths( paths );
        }
    }

    @Override
    /**
     * Configures the project.
     */
    public void configure()
    {
        createConfiguration();

        configureSDKUse();
        configureMainSourceFolder();
        configureSourcePath();
        configureLibraryPath();
        configureOutputFolderPath();
        configureManifest();
        configureTargetPlayerVersion();
        configureMainApplicationPath();
        configureAdditionalCompilerArgs();

        saveDescription();
    }

}
