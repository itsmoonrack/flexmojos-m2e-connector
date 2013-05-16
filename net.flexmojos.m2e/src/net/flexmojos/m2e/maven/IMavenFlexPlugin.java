package net.flexmojos.m2e.maven;

import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.IPath;

import com.adobe.flexbuilder.project.XMLNamespaceManifestPath;

/**
 * Defines the interface for implementation classes.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
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
     * src/main/resources are added to the class path.
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
     * Gets the locales compiled.
     */
    XMLNamespaceManifestPath[] getXMLNamespaceManifestPath();

}
