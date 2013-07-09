package net.flexmojos.m2e.maven;

import org.eclipse.core.runtime.IPath;

/**
 * Interface to abstract GeneratorMojo.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public interface IGeneratorMojo
{
    /**
     * Directory where generated files will be put.
     */
    IPath getOutputDirectory();

    /**
     * Directory where generated base files will be put.
     */
    IPath getBaseOutputDirectory();
}
