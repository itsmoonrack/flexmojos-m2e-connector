package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.actionscript.IActionScriptProject;
import com.adobe.flexbuilder.project.actionscript.internal.ActionScriptProjectSettings;
import com.adobe.flexbuilder.project.air.ApolloProjectCore;
import com.adobe.flexbuilder.project.air.IApolloActionScriptProject;
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
        final IApolloActionScriptProject apolloActionScriptProject = (IApolloActionScriptProject) ActionScriptCore.getProject( project );
        // Checks if project already exists.
        if ( apolloActionScriptProject != null )
        {
            // If it does, reuse the settings.
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
    }

    @Override
    protected void configureSDKUse()
    {
        settings.setUseFlashSDK( true );
        settings.setUseAIRConfig( true );
    }

}
