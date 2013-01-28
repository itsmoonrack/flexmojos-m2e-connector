Setup your environment
----------------------

1.  Compile and install the dependencies.

    You need first to install the m2e-flexmojos-runtime pom.xml, this is the dependencies required by the connector.
    If you have not already fetched the submodule required, run the following command in the project root:
    <pre>git submodule update</pre>
    Then, install the maven artifact by running: <pre>mvn -f m2e-flexmojos-runtime/pom.xml clean install</pre>

2.  Create a p2 repository from FlashBuilder plugins and features.

    The UpdateSite Publisher Application (org.eclipse.equinox.p2.publisher.UpdateSitePublisher) is a headless application that is capable of generating metadata (p2 repositories) from an update site containing a site.xml, bundles and features.
    
    In eclipse, create a new configuration: Run > Run Configurations... Select Eclipse Application.
    In the fieldset "Program to Run" select "Run an application" and choose org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher.
    In the Arguments tab, in the text area "Program arguments", put the following:
    <pre>
      -metadataRepository file:/&#060;some location&#062;\repository
      -artifactRepository file:/&#060;some location&#062;\repository
      -source &#060;location with a plugin and feature directory&#062;
      -configs gtk.linux.x86
      -compress
      -publishArtifacts
    </pre>

    Run the configuration.

    Note: you can have more information on creating a p2 repository in http://wiki.eclipse.org/Equinox/p2/Publisher#Features_And_Bundles_Publisher_Application.

3.  Compile the project

    Add the repository you created in your .m2/settings.xml in a profile as follow:

    &#060;profiles&#062;
      &#060;profile&#062;
        &#060;id&#062;flex-mojos&#060;/id&#062;
        &#060;repositories&#062;
          &#060;repository&#062;
            &#060;id&#062;fb47&#060;/id&#062;
            &#060;layout&#062;p2&#060;/layout&#062;
            &#060;url&#062;file:/&#060;some location&#062;\repository&#060;/url&#062;
          &#060;/repository&#062;
        &#060;/repositories&#062;
      &#060;/profile&#062;
      
      ...
      
    &#060;/profiles&#062;

    Do not forget to use this profile when invoking maven on the project!
