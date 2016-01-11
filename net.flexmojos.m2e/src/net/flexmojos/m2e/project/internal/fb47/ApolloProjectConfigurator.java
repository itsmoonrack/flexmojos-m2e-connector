package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.air.ApolloProjectCore;
import com.adobe.flexbuilder.project.air.IApolloProject;
import com.adobe.flexbuilder.project.air.export.ILaunchParameter;
import com.adobe.flexbuilder.project.air.export.ILaunchParameter.ParameterType;
import com.adobe.flexbuilder.project.air.internal.ApolloBuildTargetSettings;
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
        final IApolloProject apolloProject = ApolloProjectCore.getApolloProject( project );
        // Checks if project already exists.
        if ( apolloProject != null )
        {
            // If it does, reuse the settings.
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
        configureMainSourceFolder();
        configureSourcePath();
        configureOutputFolderPath();
        configureBuildTarget();
        configureLibraryPath();
        configureHTMLTemplate();
        configureTargetPlayerVersion();
        configureMainApplicationPath();
        configureAdditionalCompilerArgs();
    }
}
