## External Configuration

For special configuration needs there is the possibility to get the
runtime and build configuration from places outside the plugin's
configuration. This is done with the help of `<external>`
configuration sections which at least has a `<type>` subelement. This
`<type>` element selects a specific so called "handler" which is
responsible for creating the full image configuration. A handler can
decided to use the `<run>` and `<build>` configuration which could
be provided in addition to this `<external>` section or it can decide
to completely ignore any extra configuration option. 

A handler can also decide to expand this single image configuration to
a list of image configurations. The image configurations resulting
from such a external configuration are added to the *regular*
`<image>` configurations without an `<external>` section.

The available handlers are described in the following. 

#### Property based Configuration

For simple needs the image configuration can be completely defined via
Maven properties which are defined outside of this plugin's
configuration. Such a property based configuration can be selected
with an `<type>` of `props`. As extra configuration a prefix for the
properties can be defined which by default is `docker`.

Example:

```xml
<image>
  <external>
     <type>props</type>
     <prefix>docker</prefix> <!-- this is the default -->
  </external>
</image>
```

Given this example configuration a single image configuration is build
up from the following properties, which correspond to corresponding
values in the `<build>` and `<run>` sections.

* **docker.alias** Alias name
* **docker.assembly.baseDir** Directory name for the exported artifacts as
  described in an assembly (which is `/maven` by default).
* **docker.assembly.descriptor** Path to the assembly descriptor when
  building an image
* **docker.assembly.descriptorRef** Name of a predefined assembly to
  use. 
* **docker.assembly.exportBaseDir** If `true` export base directory
* **docker.assembly.ignorePermissions** If set to `true` existing file permissions are ignored
  when creating the assembly archive
* **docker.assembly.dockerFileDir** specifies a directory containing an external Dockerfile
  that will be used to create the image
* **docker.cleanup** Cleanup dangling (untagged) images after each build 
  (including any containers created from them). Default is `try` (which wont fail the build if removing fails),
  other possible values are `none` (no cleanup) or `remove` (remove but fail if unsuccessful) 
* **docker.nocache** Don't use Docker's build cache.This can be overwritten by setting a 
  system property `docker.nocache` when running Maven.
* **docker.optimise** if set to true then it will compress all the `runCmds` into a single RUN directive so that only 
one image layer is created.
* **docker.bind.idx** Sets a list of paths to bind/expose in the container
* **docker.capAdd.idx** List of kernel capabilities to add to the container
* **docker.capDrop.idx** List of kernel capabilities to remove from the container
* **docker.cmd** Command to execute. This is used both when
  running a container and as default command when creating an image.
* **docker.domainname** Container domain name
* **docker.dns.idx** List of dns servers to use
* **docker.dnsSearch.idx** List of dns search domains
* **docker.entrypoint** Container entry point
* **docker.workdir** Container working directory
* **docker.env.VARIABLE** Sets an environment
  variable. E.g. `<docker.env.JAVA_OPTS>-Xmx512m</docker.env.JAVA_OPTS>`
  sets the environment variable `JAVA_OPTS`. Multiple such entries can
  be provided. This environment is used both for building images and
  running containers. The value cannot be empty but can contain Maven property names which are
  resolved before the Dockerfile is created
* **docker.labels.LABEL** Sets a label which works similarly like setting environment variables. 
* **docker.envPropertyFile** specifies the path to a property file whose properties are 
  used as environment variables. The environment variables takes precedence over any other environment
  variables specified.
* **docker.extraHosts.idx** List of `host:ip` to add to `/etc/hosts`
* **docker.from** Base image for building an image
* **docker.hostname** Container hostname
* **docker.log.enabled** Use logging (default: `true`)
* **docker.log.prefix** Output prefix
* **docker.log.color** ANSI color to use for the prefix
* **docker.log.date** Date format for printing the timestamp
* **docker.log.driver.name** Name of an alternative log driver
* **docker.log.dirver.opts.VARIABLE** Logging driver options (specified similar as in `docker.env.VARIABLE`)
* **docker.links.idx** defines a list of links to other containers when
  starting a container. *idx* can be any suffix which is not use
  except when *idx* is numeric it specifies the order within the
  list (i.e. the list contains first a entries with numeric
  indexes sorted and the all non-numeric indexes in arbitrary order).
  For example `<docker.links.1>db</docker.links.1>` specifies a link
  to the image with alias 'db'.
* **docker.memory** Container memory (in bytes)
* **docker.memorySwap** Total memory (swap + memory) `-1` to disable swap
* **docker.name** Image name
* **docker.namingStrategy** Container naming (either `none` or `alias`)
* **docker.portPropertyFile** specifies a path to a port mapping used
  when starting a container.
* **docker.ports.idx** Sets a port mapping. For example
  `<docker.ports.1>jolokia.ports:8080<docker.ports.1>` maps
  the container port 8080 dynamically to a host port and assigns this
  host port to the Maven property `${jolokia.port}`. See
  [Port mapping](#port-mapping) for possible mapping options. When creating images images only
  the right most port is used for exposing the port. For providing multiple port mappings,
  the index should be count up. 
* **docker.registry** Registry to use for pushing images.
* **docker.restartPolicy.name** Container restart policy
* **docker.restartPolicy.retry** Max restrart retries if `on-failure` used
* **docker.user** Container user
* **docker.volumes.idx** defines a list of volumes to expose when building an image
* **docker.tags.idx** defines a list of tags to apply to a built image
* **docker.maintainer** defines the maintainer's email as used when building an image
* **docker.volumesFrom.idx** defines a list of image aliases from which
  the volumes should be mounted of the container. The list semantics
  is the same as for links (see above). For examples
  `<docker.volumesFrom.1>data</docker.volumesFrom.1>` will mount all
  volumes exported by the `data` image.
* **docker.wait.http.url** URL to wait for during startup of a container
* **docker.wait.http.method** HTTP method to use for ping check
* **docker.wait.http.status** Status code to wait for when doing HTTP ping check
* **docker.wait.time** Amount of time to wait during startup of a
    container (in ms)
* **docker.wait.log** Wait for a log output to appear.
* **wait.exec.postStart** Command to execute after the container has start up. 
* **wait.exec.preStop** Command to execute before command stops.
* **docker.wait.shutdown** Time in milliseconds to wait between stopping a container and removing it.
* **docker.wait.kill** Time in milliseconds to wait between sending SIGTERM and SIGKILL to a container when stopping it.
* **docker.workingDir** Working dir for commands to run in

Any other `<run>` or `<build>` sections are ignored when this handler
is used. Multiple property configuration handlers can be used if they
use different prefixes. As stated above the environment and ports
configuration are both used for running container and building
images. If you need a separate configuration you should use explicit
run and build configuration sections.

Example:

```xml
<properties>
  <docker.name>jolokia/demo</docker.name>
  <docker.alias>service</docker.alias>
  <docker.from>consol/tomcat:7.0</docker.from>
  <docker.assembly.descriptor>src/main/docker-assembly.xml</docker.assembly.descriptor>
  <docker.env.CATALINA_OPTS>-Xmx32m</docker.env.CATALINA_OPTS>
  <docker.label.version>${project.version}</docker.label.version>
  <docker.ports.jolokia.port>8080</docker.ports.jolokia.port>
  <docker.wait.url>http://localhost:${jolokia.port}/jolokia</docker.wait.url>
</properties>

<build>
  <plugins>
    <plugin>
      <groupId>io.fabric8</groupId>
      <artifactId>docker-maven-plugin</artifactId>
      <configuration>
        <images>
          <image>
            <external>
              <type>props</type>
              <prefix>docker</prefix>
            </external>
          </image>
        </images>
      </configuration>
    </plugin>
  </plugins>
</build>
```

