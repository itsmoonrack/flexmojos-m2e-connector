package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.IFlexProject;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.internal.FlexProjectSettings;
import com.google.inject.Inject;

public class FlexProjectConfigurator
    extends AbstractFlexProjectConfigurator
{

    @Inject FlexProjectConfigurator( final IMavenFlexPlugin plugin,
                                     final IProject project,
                                     final IProgressMonitor monitor )
    {
        super( plugin, project, monitor );
    }

    @Override
    protected void createConfiguration()
    {
        final IFlexProject flexProject = (IFlexProject) ActionScriptCore.getProject( project );
        // Checks if project already exists.
        if ( flexProject != null )
        {
            // If it does, reuse the settings.
            settings = flexProject.getFlexProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.
            settings = FlexProjectManager
                            .createFlexProjectDescription( project.getName(),
                                                           project.getLocation(),
                                                           false /* FIXME : hard - coded ! */,
                                                           FlexServerType.NO_SERVER /* FIXME : hard - coded ! */);
        }
    }

    @Override
    protected void saveDescription()
    {
        final FlexProjectSettings flexProjectSettings = (FlexProjectSettings) settings;
        flexProjectSettings.saveDescription( project, monitor );
    }

    @Override
    protected void configureSDKUse()
    {
        settings.setUseFlashSDK( false );
        settings.setUseAIRConfig( false );
    }

    @Override
    protected void configureLibraryPath()
    {
        super.configureFlexSDKName();
        settings.setDefaultLinkType( IClassPathEntry.LINK_TYPE_RSL );
        super.configureLibraryPath();
    }

}
