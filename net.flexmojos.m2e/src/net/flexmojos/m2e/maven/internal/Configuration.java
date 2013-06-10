package net.flexmojos.m2e.maven.internal;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Simple configuration wrapper class.
 *
 * Maven plug-ins (build and reporting) are configured by specifying a <code><configuration></code> element where the
 * child elements of the <code><configuration></code> element are mapped to fields inside the Mojo. Because a plug-in
 * consists of one or more Mojos where a Mojo maps to a goal.
 *
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class Configuration
{
    private final Xpp3Dom configuration;

    private final ExpressionEvaluator evaluator;

    Configuration(final MavenSession session, final MojoExecution mojoExecution)
    {
        this.configuration = mojoExecution.getConfiguration();
        this.evaluator = new PluginParameterExpressionEvaluator( session, mojoExecution );
    }

    /**
     * Short-hand method for evaluating a configuration value.
     *
     * @return the configuration value if defined, or the default value if not.
     */
    public String evaluate( final String name )
    {
        try
        {
            final Xpp3Dom child = configuration.getChild( name );
            if ( child.getValue() != null )
                return (String) evaluator.evaluate( child.getValue() );
            else
                return (String) evaluator.evaluate( child.getAttribute( "default-value" ) );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
