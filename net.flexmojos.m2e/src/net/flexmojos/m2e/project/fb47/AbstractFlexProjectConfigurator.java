package net.flexmojos.m2e.project.fb47;

import net.flexmojos.m2e.flex.FlexFrameworkHelper;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.google.inject.Inject;

public abstract class AbstractFlexProjectConfigurator
    extends ActionScriptProjectConfigurator
{

    protected AbstractFlexProjectConfigurator( final IMavenFlexPlugin plugin )
    {
        super( plugin );
    }

    @Inject
    public AbstractFlexProjectConfigurator( final IMavenProjectFacade facade, final IProgressMonitor monitor,
                                            final IMavenFlexPlugin plugin )
    {
        super( facade, monitor, plugin );
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
