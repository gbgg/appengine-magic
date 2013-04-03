# Appengine-Magic (derived)

## Status

This is derived from appengine-magic.  It's basically a reorganization
of the codebase to make it conform to leiningen 2 patterns.  It is not
finished, so don't bother downloading it and trying to use it- it
won't work.

## Structure

 * API - the implementation has the usual kernel-and-hull structure:
   * kernel - kernel api only; this involves very little code.
   * services - most of the GAE functionality is implemented as "services"
     (e.g. datastore, user, channel, etc.) ; each service is packaged
     as a separate jar so you can pick and choose; a "services
     uberjar" containing all of them is also provided.
 * jetty - a dev/test server embedding jetty without servlet container
   services.  Supports rapid interactive (repl-based) development at
   the cost of not completely emulating the GAE environment.
 * magic - leinigen plugin implementing tasks to build your project,
   deploy to google, etc.  In particular, commands to run either the
   official GAE devserver or the appengine-magic jetty server for
   local development and testing.

## Libraries

 * appengine-magic lib
 * appengine-magic services
 * appengine-magic service user
 * appengine-magic service datastore
 * etc.

## Installation

    [appengine-magic/lib "x.y.z"]
    [appengine-magic/services "x.y.z"]
    [appengine-magic/service/user "x.y.z"]
    [appengine-magic/service/datastore "x.y.z"]
    etc.
    [appengine-magic "x.y.z"] ;; everything

## Developing appengine-magic applications

### devserver and magic server.

You can use the Google sdk-supplied dev server ("devserver" for short)
to test your app, but of course you don't get the interactive
repl-based joyosity treasured by clojurians.  For that you have to use
the appengine-magic server ("magic server" for short).  However, the
magic server does not behave like the devserver; for example, it does
not read your web.xml deployment descriptor and it doesn't set the
context root like a real servlet container.  The magic server is an
interim solution until we can figure out how to run the devserver in a
repl.  So you can use the magic server to do rapid interactive
development but you will always need to test with the devappserver
before deploying.

Here's what you need to know to use the magic server for development.
This assumes that you are using [compojure](git://github.com/weavejester/compojure.git) and [ring](https://github.com/ring-clojure/ring).

#### Servlet config and routing.

Google App Engine for java is basically a servlet container.  So your
application will implement one or more servlets, and you use
war/WEB-INF/web.xml to configure them.  For example, if you want
servlet "frob.nicate" (that's a clojure namespace, corresponding to
source code in src/frob/nicate.clj) to service requests to
http://example.org/frobnicate, you would put the following in your
web.xml:

```xml
<servlet>
  <servlet-name>frobber</servlet-name>
  <servlet-class>frob.nicate</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>frobber</servlet-name>
  <url-pattern>/frobnicate/*</url-pattern>
</servlet-mapping>
```

Note that you can have one servlet service multiple paths.  So in addition to the above let's add:

```xml
<servlet>
  <servlet-name>defrobber</servlet-name>
  <servlet-class>frob.nicate</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>defrobber</servlet-name>
  <url-pattern>/defrob/*</url-pattern>
</servlet-mapping>
```

Now a request to frob a doobsnickers
(http://example.org/frobnicate/doobsnickers) or defrob
(http://example.org/defrob/doobsnickers) will be routed to your
frob.nicate servlet for handling:

```clojure
(GET "/frobnicate/:widget" [widget] ... handle request
(GET "/defrob/:widget" [widget] ... handle request
```

#### Multiple Servlets

The magic server only supports a single servlet, and it
programmatically sets its context to "/".  You tell it which servlet
to use when you start it by calling

```clojure
(appengine-magic.core/start
```

In other words, it does **not** read your web.xml file.  But it's easy
to test multiple servlets; all it takes is is a few trivial clojure
functions that load the relevant code and then execute a restart
command on the server.  For an example, see the :repl-options key of
the project.clj file example produced by

```shell
$ lein new appengine-magic ...
```

That code defines two functions named after the two servlets
implemented by the project.  To switch from one servlet to another all
you need to do is execute the appropriate function as a command at the
repl prompt; for example:
```clojure
user=> (user)
```

This reloads (and thus re-evaluates) the code in user.clj and then
restarts the magic server with myproj-user as the handler.

The only major drawback is you won't be able to test servlets that
talk to each other in the magic server; you'll have to use the
devserver for that.