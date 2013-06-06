package net.flexmojos.m2e;

import net.flexmojos.m2e.project.AbstractConfigurator;
import net.flexmojos.m2e.project.fb47.ActionScriptProjectConfigurator;
import net.flexmojos.m2e.project.fb47.ApolloActionScriptProjectConfigurator;
import net.flexmojos.m2e.project.fb47.ApolloProjectConfigurator;
import net.flexmojos.m2e.project.fb47.FlexLibraryProjectConfigurator;
import net.flexmojos.m2e.project.fb47.FlexProjectConfigurator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

public class FlashBuilder47Module
    extends FlashBuilderAbstractModule
{

    public FlashBuilder47Module( final ProjectConfigurationRequest request, final IProgressMonitor monitor )
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
