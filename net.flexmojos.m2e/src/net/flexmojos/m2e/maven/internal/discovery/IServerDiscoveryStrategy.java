package net.flexmojos.m2e.maven.internal.discovery;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.IPath;

public interface IServerDiscoveryStrategy
{
    /**
     * Whether or not the concrete strategy is a server for the given project.
     *
     * This method does not check if the folder exists nor creates it, just indicates if a project has a server to host
     * its output.
     *
     * @param project
     * @return
     */
    boolean hasServer(MavenProject project);

    /**
     * Returns the folder path of the server.
     *
     * @return
     */
    IPath getOutputFolderPath();
}
