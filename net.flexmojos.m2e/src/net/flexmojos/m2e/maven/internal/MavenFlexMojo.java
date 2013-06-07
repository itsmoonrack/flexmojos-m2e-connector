package net.flexmojos.m2e.maven.internal;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public abstract class MavenFlexMojo
{
    protected ExpressionEvaluator evaluator;

    protected Xpp3Dom configuration;

    protected MavenFlexMojo( final MavenSession session, final MojoExecution mojoExecution )
    {
        this.evaluator = new PluginParameterExpressionEvaluator( session, mojoExecution );
        this.configuration = mojoExecution.getConfiguration();
    }

    /**
     * Short-hand method for evaluating a configuration value.
     *
     * @return
     */
    protected String evaluate( final Xpp3Dom conf )
    {
        try
        {
            if ( conf.getValue() != null )
                return (String) evaluator.evaluate( conf.getValue() );
            else
                return (String) evaluator.evaluate( conf.getAttribute( "default-value" ) );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
