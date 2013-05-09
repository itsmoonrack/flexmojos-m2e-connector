package net.flexmojos.m2e;

import static net.flexmojos.oss.plugin.common.FlexExtension.AIR;
import static net.flexmojos.oss.plugin.common.FlexExtension.SWC;

import java.util.List;
import java.util.Map;

import net.flexmojos.m2e.maven.Flexmojos6Adapter;
import net.flexmojos.m2e.maven.IMavenFlexPlugin;
import net.flexmojos.m2e.project.AbstractConfigurator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.google.inject.AbstractModule;

/**
 * Flash Builder abstract components configuration. Depending on the version of FlashBuilder, a specialized version of
 * this module refines the implementation of the abstraction interface AbstractProjectConfigurator.
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 */
public abstract class FlashBuilderAbstractModule
    extends AbstractModule
{

    private final IMavenProjectFacade facade;

    private final IProgressMonitor monitor;

    private final MavenSession session;

    public FlashBuilderAbstractModule(final ProjectConfigurationRequest request, final IProgressMonitor monitor)
    {
        facade = request.getMavenProjectFacade();
        this.monitor = monitor;
        session = request.getMavenSession();
    }

    @Override
    protected void configure()
    {
        final IProject project = facade.getProject();

        // Adds the ActionScript nature.
        addNature(project, "com.adobe.flexbuilder.project.actionscriptnature", monitor);
        // Sets the base project configurator to an ActionScript project configurator. While a project can have multiple
        // natures, a project can not have more than one configurator. The algorithm bellow is based on "the last
        // assignment is the right one" adding natures to the project as the execution flow goes into the branches but
        // overriding configurators to eventually define the project.
        Class<? extends AbstractConfigurator> configurator = getActionScriptProjectConfiguratorClass();

        if (isApolloProject())
        {
            // An Apollo project exists in two flavors: ApolloActionScriptProject, and ApolloProject. While the former
            // directly extends from ActionScriptProject, the later inherits from FlexProject, so it is perfectly
            // possible for an Apollo project to have a Flex nature as well.
            addNature(project, "com.adobe.flexbuilder.project.apollonature", monitor);
            // The configurator will replace the ActionScript project configurator initially set by an
            // ApolloActionScript
            // project configurator. Later in the execution flow, in the case a project have the Flex nature as well,
            // the
            // configurator will be replaced by a "pure" Apollo project configurator.
            configurator = getApolloActionScriptProjectConfiguratorClass();
        }

        if (isFlexProject())
        {
            // Depending on the packaging, a Flex project can be a FlexLibraryProject (SWC), a FlexProject (SWF) or an
            // ApolloProject (AIR).
            if (SWC.equals(facade.getPackaging()))
            {
                addNature(project, "com.adobe.flexbuilder.project.flexlibnature", monitor);
                configurator = getFlexLibraryProjectConfiguratorClass();
                // End of algorithm.
            }
            else
            {
                // An AIR and SWF packaging indicates respectively an ApolloProject and a FlexProject, in both case the
                // Flex
                // nature is added to the project.
                addNature(project, "com.adobe.flexbuilder.project.flexnature", monitor);
                if (AIR.equals(facade.getPackaging()))
                {
                    configurator = getApolloProjectConfiguratorClass();
                    // End of algorithm.
                }
                else
                {
                    configurator = getFlexProjectConfiguratorClass();
                    // End of algorithm.
                }
            }
        }
        else if (SWC.equals(facade.getPackaging()))
        {
            // In the case there is no declared Flex dependencies, and the packaging is SWC, its an ActionScriptProject
            // with
            // an aslib nature.
            addNature(project, "com.adobe.flexbuilder.project.aslibnature", monitor);
            // End of algorithm.
        }

        bind(IProgressMonitor.class).toInstance(monitor);
        bind(IMavenProjectFacade.class).toInstance(facade);
        bind(IMavenFlexPlugin.class).to(getMavenFlexPluginClass());
        bind(AbstractConfigurator.class).to(configurator);
        bind(MavenSession.class).toInstance(session);
    }

    protected abstract Class<? extends AbstractConfigurator> getActionScriptProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getApolloActionScriptProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getFlexLibraryProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getApolloProjectConfiguratorClass();

    protected abstract Class<? extends AbstractConfigurator> getFlexProjectConfiguratorClass();

    /**
     * Short-hand method for wrapping an "addNature" operation.
     * @param project
     * @param natureId
     * @param monitor
     */
    private void addNature(final IProject project, final String natureId, final IProgressMonitor monitor)
    {
        try
        {
            org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator.addNature(project, natureId, monitor);
        }
        catch (final CoreException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the IMavenFlexPlugin implementation.
     * @return
     * @throws CoreException
     */
    private Class<? extends IMavenFlexPlugin> getMavenFlexPluginClass()
    {
        try
        {
            List<MojoExecution> flexmojos = null;
            MojoExecution mojo = null;

            // Checks the net.flexmojos.oss plug-in exists.
            flexmojos =
                facade.getMojoExecutions("net.flexmojos.oss", "flexmojos-maven-plugin", monitor, "compile-swf",
                    "compile-swc");

            if (flexmojos.size() != 0)
            {
                bind(MojoExecution.class).toInstance(mojo = flexmojos.get(0));
                // If it does, get the configuration from the mojo.
                if (mojo.getVersion().startsWith("6"))
                    return Flexmojos6Adapter.class;
            }

            // Checks the org.sonatype.flexmojos plug-in exists.
            flexmojos =
                facade.getMojoExecutions("org.sonatype.flexmojos", "flexmojos-maven-plugin", monitor, "compile-swf",
                    "compile-swc");

            return null;
        }
        catch (final CoreException e)
        {
            // Inform user the Maven Flex plugin could not be found.
            throw new RuntimeException("Maven Flex plugin not found.");
        }
    }

    private boolean isFlexProject()
    {
        final Map<String, Artifact> dependencies = facade.getMavenProject().getArtifactMap();
        // Supports both Adobe and Apache groupId.
        return dependencies.containsKey("com.adobe.flex.framework:flex-framework")
            || dependencies.containsKey("org.apache.flex.framework:flex-framework");
    }

    private boolean isApolloProject()
    {
        // TODO: implement me !
        return false;
    }

}
