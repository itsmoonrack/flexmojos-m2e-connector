Setup your environment
----------------------

1.  Create the Flash Builder 4.x target platform as a p2 repository.

    The Features and Bundles Publisher Application (org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher) is a headless application that is capable of generating metadata (p2 repositories) from pre-build Eclipse bundles and features.
    
    In eclipse, create a new configuration: Run > Run Configurations... Select Eclipse Application.
    In the fieldset "Program to Run" select "Run an application" and choose org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher.
    In the Arguments tab, put the following in the text area "Program arguments":

        -metadataRepository file:<some location>\repository
        -artifactRepository file:<some location>\repository
        -source <location with a plugin and feature directory>;
        -configs win32.win32.x86_64
        -compress
        -publishArtifacts

    In this example, the plugins in **/&lt;location with a plugin and feature directory&gt;/plugins** and features in **/&lt;location with a plugin and feature directory&gt;/features** will be published in the **file:/&lt;some location&gt;/repository** repository. The artifacts will also be published, and the repositories (artifacts.xml and content.xml) compressed.
    Run the configuration and repeat this step for each Flash Builder installation you have: 4.0, 4.5, 4.6 and 4.7. This will create different target platform.

    Note: you can have more information on creating a p2 repository in http://wiki.eclipse.org/Equinox/p2/Publisher#Features_And_Bundles_Publisher_Application.

2.  Clean and install the dependencies.

    If you have not already fetched the git submodule required (m2e-core), run the following command in the project root:
    <pre>git submodule update</pre>
    This will pull the tag 1.2.0 of the m2e-core repository in your project, it is required as m2e-flexmojos is built against this version.

    You need to install the provided maven module *m2e-flexmojos-runtime*, this maven module contains the runtime dependencies required by the connector.
    Install the maven artifact in your local repository by running: <pre>mvn -f m2e-flexmojos-runtime/pom.xml clean install</pre>

3.  Compile the project.

    Add the repository you created in your .m2/settings.xml in a profile as follow:

        <profiles>
          <profile>
            <id>m2e-flexmojos</id>
            <repositories>
              <repository>
                <id>fb-46</id>
                <layout>p2</layout>
                <url>file:/<some location>\repository</url>
              </repository>
              
              <repository>
                <id>fb-47</id>
                <layout>p2</layout>
                <url>file:/<some location>\repository</url>
              </repository>
            </repositories>
          </profile>
          
          ...
          
        </profiles>

    Do not forget to use this profile when invoking maven on the project! It will allows Tycho to use the Flash Builder 4.x platform when building the m2e connector plugin. Note that you will also need to set-up a new Target Definition in eclipse to use the plugins and features of your Flash Builder installation.
    
    Run mvn clean install on the project root. You can also use the buildall.sh script once you declared the p2 repository in your maven user's settings.xml.

4.  Run and Debug from eclipse.

    Creates the Flash Builder 4.x target by opening preferences in Window > Preferences. In Plug-in Development > Target Platform, click on *Add...*

    Initialize the target definition with **Nothing: Start with an empty target definition**. Click on *Next >*.

    In the *Locations* tab, click on *Add...* and select your eclipse Flash Builder directory (the directory must contain a plugins and features directory).
    
    In the *Environment* tab, check that the Execution Environment is set to a JavaSE-1.6 compatible JRE.
    
    In the *Arguments* tab, add the following in the VM arguments text area:
        -Xms512m
        -Xmx1024m
        -XX:MaxPermSize=256m
        -XX:PermSize=64m

    Call this target platform Flash Builder 4.x, and make sure it is the only platform activated.

    When you launch the plug-in in run or debug mode, it will create an Eclipse Application in your Run/Debug Configuration. Open it and navigates to the Plug-ins tab, in the *Launch with:* drop-down, choose plugin-ins selected bellow only. Un-thick **Workspace** check-box, and make sure all plug-ins under **Target Platform** are thick.
