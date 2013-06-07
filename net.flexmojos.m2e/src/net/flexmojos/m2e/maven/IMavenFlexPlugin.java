package net.flexmojos.m2e.maven;

import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.IPath;

/**
 * Defines the interface for plug-in implementation classes.
 *
 * The MavenFlexPlugin represents configuration of plug-ins as a whole. It reflects the MavenProjectFacade plus build
 * plug-ins involved in the configuration of the project as it is often composed of multiple components for different
 * goals such as compile-swf, sign-air and generate sources. These settings needs to be merged together so the
 * AbstractConfigurator can apply settings to the project accordingly.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 * @author Sebastien Pinel
 */
public interface IMavenFlexPlugin
{

    /**
     * Gets main source folder.
     */
    IPath getMainSourceFolder();

    /**
     * Gets Flex Framework artifact.
     */
    Artifact getFlexFramework();

    /**
     * Gets the source path so the testSourceDirectory, and additional resources locations such as default
     * src/main/resources or baseOutputDirectory for "generate" goal are added to the class path.
     */
    IPath[] getSourcePath();

    /**
     * Gets the target player version.
     */
    String getTargetPlayerVersion();

    /**
     * Gets the main application path.
     */
    IPath getMainApplicationPath();

    /**
     * Gets the output folder path.
     */
    IPath getOutputFolderPath();

    /**
     * Gets the certificate folder path.
     */
    IPath getCertificatePath();

    /**
     * Gets the artifacts by adding Maven's SWC dependencies of the project.
     */
    Map<String, Artifact> getDependencies();

    /**
     * Gets the locale source path.
     */
    IPath getLocalesSourcePath();

    /**
     * Gets the locales compiled.
     */
    String[] getLocalesCompiled();

    /**
     * Gets the XML name-space manifest path map.
     */
    Map<String, IPath> getXMLNamespaceManifestPath();

}
