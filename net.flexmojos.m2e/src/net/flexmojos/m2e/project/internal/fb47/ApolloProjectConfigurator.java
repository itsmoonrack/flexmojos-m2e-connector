package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.actionscript.ActionScriptCore;
import com.adobe.flexbuilder.project.actionscript.IActionScriptProject;
import com.adobe.flexbuilder.project.air.ApolloProjectCore;
import com.adobe.flexbuilder.project.air.IApolloProject;
import com.adobe.flexbuilder.project.air.export.ILaunchParameter;
import com.adobe.flexbuilder.project.air.export.ILaunchParameter.ParameterType;
import com.adobe.flexbuilder.project.air.internal.ApolloBuildTargetSettings;
import com.adobe.flexbuilder.project.air.internal.ApolloProject;
import com.adobe.flexbuilder.project.air.internal.ApolloProjectSettings;
import com.google.inject.Inject;

public class ApolloProjectConfigurator
    extends AbstractFlexProjectConfigurator
{

    @Inject ApolloProjectConfigurator( final IMavenFlexPlugin plugin,
                                       final IProject project,
                                       final IProgressMonitor monitor )
    {
        super( plugin, project, monitor );
    }

    @Override
    protected void createConfiguration()
    {
        final IActionScriptProject unknownProject = ActionScriptCore.getProject( project );
        final IApolloProject apolloProject = unknownProject.getClass() == ApolloProject.class
            ? (IApolloProject) unknownProject : null;
        // Checks if project already exists.
        if ( apolloProject != null )
        {
            // If it does, reuse the settings and project.
            adobeProject = apolloProject;
            settings = apolloProject.getFlexProjectSettingsClone();
        }
        else
        {
            // If it does not, create new settings.
            settings = ApolloProjectCore
                            .createApolloSettings( project.getName(),
                                                   project.getLocation(),
                                                   FlexServerType.NO_SERVER /* FIXME : hard - coded ! */);
        }
    }

    @Override
    protected void saveDescription()
    {
        final ApolloProjectSettings apolloProjectSettings = (ApolloProjectSettings) settings;
        apolloProjectSettings.saveDescription( project, monitor );

        // Creats project if dose not exists
        if ( adobeProject == null )
        {
            try
            {
                adobeProject = new ApolloProject( apolloProjectSettings, project, monitor );
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
        settings.setUseFlashSDK( false );
        settings.setUseAIRConfig( true );
    }

    @Override
    protected void configureLibraryPath()
    {
        super.configureFlexSDKName();
        super.configureLibraryPath();
    }

    protected void configureBuildTarget()
    {
        final ApolloProjectSettings apolloProjectSettings = (ApolloProjectSettings) settings;
        final ApolloBuildTargetSettings buildTargetSettings =
            new ApolloBuildTargetSettings( ApolloBuildTargetSettings.DEFAULT_PLATFORM_ID,
                                           ApolloBuildTargetSettings.DEFAULT_BUILD_TARGET_NAME,
                                           ApolloBuildTargetSettings.DEFAULT_BUILD_TARGET_NAME );
        buildTargetSettings.setCertificatePath( plugin.getCertificatePath() );
        buildTargetSettings.setAirExcludePaths( new IPath[0] );
        buildTargetSettings.setANEPaths( new IPath[0] );
        buildTargetSettings.setTimestamp( true );
        buildTargetSettings.setAddedParameters( new ILaunchParameter[0], ParameterType.LAUNCHING );
        buildTargetSettings.setAddedParameters( new ILaunchParameter[0], ParameterType.PACKAGING );
        buildTargetSettings.setModifiedParameters( new ILaunchParameter[0], ParameterType.LAUNCHING );
        buildTargetSettings.setModifiedParameters( new ILaunchParameter[0], ParameterType.PACKAGING );
        apolloProjectSettings.setBuildTargetSettings( ApolloBuildTargetSettings.DEFAULT_PLATFORM_ID,
                                                      ApolloBuildTargetSettings.DEFAULT_BUILD_TARGET_NAME,
                                                      buildTargetSettings );

    }

    @Override
    public void configure()
    {
        createConfiguration();

        configureSDKUse();
        configureMainSourceFolder();
        configureSourcePath();
        configureOutputFolderPath();
        configureBuildTarget();
        configureLibraryPath();
        configureHTMLTemplate();
        configureTargetPlayerVersion();
        configureMainApplicationPath();
        configureAdditionalCompilerArgs();

        saveDescription();
    }
}
