package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.flex.FlexFrameworkHelper;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.inject.Inject;

public abstract class AbstractFlexProjectConfigurator
    extends ActionScriptProjectConfigurator
{

    @Inject AbstractFlexProjectConfigurator( final IMavenFlexPlugin plugin,
                                             final IProject project,
                                             final IProgressMonitor monitor )
    {
        super( plugin, project, monitor );
    }

    /**
     * Configures the Flex SDK name and adds it to the library path of the project. Must be called before configuring
     * the library path.
     */
    protected void configureFlexSDKName()
    {
        final String flexVersion = plugin.getFlexFramework().getVersion();
        final String flexSDKName = FlexFrameworkHelper.getFlexSDKName( flexVersion );
        settings.setFlexSDKName( flexSDKName );
    }
}
