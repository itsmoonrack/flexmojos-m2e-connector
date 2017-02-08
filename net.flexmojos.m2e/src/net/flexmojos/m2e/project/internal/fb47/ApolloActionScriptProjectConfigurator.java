package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.actionscript.IActionScriptProject;
import com.adobe.flexbuilder.project.air.ApolloProjectCore;
import com.adobe.flexbuilder.project.air.IApolloActionScriptProject;
import com.adobe.flexbuilder.project.air.internal.ApolloActionScriptProject;
import com.adobe.flexbuilder.project.air.internal.ApolloActionScriptProjectSettings;
import com.google.inject.Inject;

public class ApolloActionScriptProjectConfigurator
    extends ActionScriptProjectConfigurator
{

    @Inject ApolloActionScriptProjectConfigurator( final IMavenFlexPlugin plugin,
                                                   final IProject project,
                                                   final IProgressMonitor monitor )
    {
        super( plugin, project, monitor );
    }

    @Override
    protected void createConfiguration()
    {
        final IActionScriptProject unknownProject = ActionScriptCore.getProject( project );
        final IApolloActionScriptProject apolloActionScriptProject = unknownProject.getClass() == ApolloActionScriptProject.class
            ? (IApolloActionScriptProject) unknownProject : null;
        // Checks if project already exists.
        if ( apolloActionScriptProject != null )
        {
            // If it does, reuse the settings and project.
            adobeProject = apolloActionScriptProject;
            settings = apolloActionScriptProject.getProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.
            settings = ApolloProjectCore
                            .createApolloActionScriptSettings( project.getName(),
                                                               project.getLocation());
        }
    }

    @Override
    protected void saveDescription()
    {
        final ApolloActionScriptProjectSettings apolloActionScriptProjectSettings = (ApolloActionScriptProjectSettings) settings;
        apolloActionScriptProjectSettings.saveDescription( project, monitor );

        // Creats project if dose not exists
        if ( adobeProject == null )
        {
            try
            {
                adobeProject = new ApolloActionScriptProject( apolloActionScriptProjectSettings, project, monitor );
            }
            catch ( final CoreException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    @Override
    protected void configureSDKUse()
    {
        settings.setUseFlashSDK( true );
        settings.setUseAIRConfig( true );
    }

}
