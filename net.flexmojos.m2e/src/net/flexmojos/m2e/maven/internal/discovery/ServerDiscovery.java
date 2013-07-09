package net.flexmojos.m2e.maven.internal.discovery;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.IPath;

public class ServerDiscovery implements IServerDiscoveryStrategy
{

    @Override
    public boolean hasServer( final MavenProject project )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IPath getOutputFolderPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
