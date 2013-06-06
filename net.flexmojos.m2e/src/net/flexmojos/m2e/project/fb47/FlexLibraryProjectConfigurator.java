package net.flexmojos.m2e.project.fb47;

import java.util.Map;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.IFlexLibraryProject;
import com.adobe.flexbuilder.project.XMLNamespaceManifestPath;
import com.adobe.flexbuilder.project.internal.FlexLibraryProjectSettings;
import com.google.inject.Inject;

public class FlexLibraryProjectConfigurator
extends AbstractFlexProjectConfigurator
{

    protected FlexLibraryProjectConfigurator( final IMavenFlexPlugin plugin )
    {
        super( plugin );
    }

    @Inject
    public FlexLibraryProjectConfigurator( final IMavenProjectFacade facade, final IProgressMonitor monitor,
                                           final IMavenFlexPlugin plugin )
    {
        super( plugin );
        this.monitor = monitor;
        project = facade.getProject();

        final IFlexLibraryProject flexProject = (IFlexLibraryProject) FlexProjectManager.getFlexProject( project );
        // Checks if project already exists.
        if ( flexProject != null )
        {
            // If it does, reuse the settings.
            settings = flexProject.getFlexLibraryProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.
            settings =
                            FlexProjectManager.createFlexLibraryProjectDescription( project.getName(), project.getLocation(), false /*
                             * FIXME
                             * :
                             * hard
                             * -
                             * coded
                             * !
                             */);
        }
    }

    @Override
    public void saveDescription()
    {
        final FlexLibraryProjectSettings flexProjectSettings = (FlexLibraryProjectSettings) settings;
        flexProjectSettings.saveDescription( project, monitor );
    }

    @Override
    protected void configureLibraryPath()
    {
        super.configureFlexSDKName();
        super.configureLibraryPath();
    }

    protected void configureManifest()
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

    @Override
    /**
     * Configures the project.
     */
    public void configure()
    {
        configureMainSourceFolder();
        configureSourcePath();
        configureLibraryPath();
        configureOutputFolderPath();
        configureManifest();
        configureTargetPlayerVersion();
        configureMainApplicationPath();
        configureAdditionalCompilerArgs();
    }

}
