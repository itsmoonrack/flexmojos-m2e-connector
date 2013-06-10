package net.flexmojos.m2e.project.internal.fb47;

import net.flexmojos.m2e.FlashBuilderAbstractModule;
import net.flexmojos.m2e.project.AbstractConfigurator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

public class FlashBuilder47Module extends FlashBuilderAbstractModule
{

    public FlashBuilder47Module( final ProjectConfigurationRequest request,
                                 final IProgressMonitor monitor )
    {
        super( request, monitor );
    }

    @Override
    protected Class<? extends AbstractConfigurator> getActionScriptProjectConfiguratorClass()
    {
        return ActionScriptProjectConfigurator.class;
    }

    @Override
    protected Class<? extends AbstractConfigurator> getApolloActionScriptProjectConfiguratorClass()
    {
        return ApolloActionScriptProjectConfigurator.class;
    }

    @Override
    protected Class<? extends AbstractConfigurator> getFlexLibraryProjectConfiguratorClass()
    {
        return FlexLibraryProjectConfigurator.class;
    }

    @Override
    protected Class<? extends AbstractConfigurator> getApolloProjectConfiguratorClass()
    {
        return ApolloProjectConfigurator.class;
    }

    @Override
    protected Class<? extends AbstractConfigurator> getFlexProjectConfiguratorClass()
    {
        return FlexProjectConfigurator.class;
    }

}
