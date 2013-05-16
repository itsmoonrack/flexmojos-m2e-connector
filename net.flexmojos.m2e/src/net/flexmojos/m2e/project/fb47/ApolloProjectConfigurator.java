package net.flexmojos.m2e.project.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.air.ApolloProjectCore;
import com.adobe.flexbuilder.project.air.IApolloProject;
import com.adobe.flexbuilder.project.air.internal.ApolloProjectSettings;
import com.google.inject.Inject;

public class ApolloProjectConfigurator
    extends AbstractFlexProjectConfigurator
{

    @Inject
    public ApolloProjectConfigurator( final IMavenProjectFacade facade, final IProgressMonitor monitor,
                                      final IMavenFlexPlugin plugin )
    {
        super( plugin );
        this.monitor = monitor;
        project = facade.getProject();
        final IApolloProject apolloProject = ApolloProjectCore.getApolloProject( project );

        if ( apolloProject != null )
        {
            settings = apolloProject.getFlexProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.

            settings =
                ApolloProjectCore.createApolloSettings( project.getName(), project.getLocation(),
                                                        FlexServerType.NO_SERVER
                /* FIXME : hard - coded ! */);
        }
    }

    @Override
    public void saveDescription()
    {
        final ApolloProjectSettings apolloProjectSettings = (ApolloProjectSettings) settings;
        apolloProjectSettings.saveDescription( project, monitor );
    }

    @Override
    protected void configureLibraryPath()
    {
        super.configureFlexSDKName();
        super.configureLibraryPath();
    }

}
