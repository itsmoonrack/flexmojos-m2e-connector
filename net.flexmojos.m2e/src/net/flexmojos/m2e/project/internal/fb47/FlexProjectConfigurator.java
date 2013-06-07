package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.IClassPathEntry;
import com.adobe.flexbuilder.project.IFlexProject;
import com.adobe.flexbuilder.project.internal.FlexProjectSettings;
import com.google.inject.Inject;

public class FlexProjectConfigurator
    extends AbstractFlexProjectConfigurator
{

    @Inject
    public FlexProjectConfigurator( final IMavenProjectFacade facade, final IProgressMonitor monitor,
                                    final IMavenFlexPlugin plugin )
    {
        super( plugin );
        this.monitor = monitor;
        project = facade.getProject();

        final IFlexProject flexProject = FlexProjectManager.getFlexProject( project );
        // Checks if project already exists.
        if ( flexProject != null )
        {
            // If it does, reuse the settings.
            settings = flexProject.getFlexProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.
            settings =
                FlexProjectManager.createFlexProjectDescription( project.getName(), project.getLocation(), false /*
                                                                                                                  * FIXME:
                                                                                                                  * hard
                                                                                                                  * -
                                                                                                                  * coded
                                                                                                                  * !
                                                                                                                  */,
                                                                 FlexServerType.NO_SERVER
                /* FIXME : hard - coded ! */);
        }
    }

    @Override
    public void saveDescription()
    {
        final FlexProjectSettings flexProjectSettings = (FlexProjectSettings) settings;
        flexProjectSettings.saveDescription( project, monitor );
    }

    @Override
    protected void configureLibraryPath()
    {
        super.configureFlexSDKName();
        settings.setDefaultLinkType( IClassPathEntry.LINK_TYPE_RSL );
        super.configureLibraryPath();
    }

}
