# Appengine-Magic

## Status

This is derived from appengine-magic.  It's basically a reorganization
of the codebase to make it conform to leiningen 2 patterns.  It is not
finished, so don't bother downloading it and trying to use it- it
won't work.

## Structure

 * lib - api only

 * server - repl-enabled emulation of the sdk dev server.  **CAVEAT**
   The appengine-magic server does _not_ behave like the google dev
   server (invoked via &lt;sdk_home&gt;/bin/dev_appserver.sh).  See below
   for details.

 * magic - leinigen plugin implementing tasks to build your project,
   run the magic server, deploy to google, etc.

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

### Dev server.

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

 * Google App Engine for java is basically a servlet container.  So
   your application will implement one or more servlets, and you use
   war/WEB-INF/web.xml to configure them.  For example, if you want
   servlet "frob.nicate" (that's a clojure namespace, corresponding to
   source code in src/frob/nicate.clj) to service requests to
   http://example.org/frobnicate, you would put the following in your
   web.xml:

	<servlet>
	    <servlet-name>frobber</servlet-name>
	        <servlet-class>frob.nicate</servlet-class>
	</servlet>
	<servlet-mapping>
	    <servlet-name>frobber</servlet-name>
	    <url-pattern>/frobnicate/*</url-pattern>
	</servlet-mapping>

   Now you might think that in your frob/nicate.clj source file, you
   will set up routes and handlers like so:

```(GET "/frobnicate/:widget" [widget]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (format "frobbing widget %s" widget)})```

   And in fact this would work with the magic server; but it won't
   work with the dev server.  Because of the servlet mapping, a
   request to http:www.example.org/frobnicate will be mapped to "/" by
   the time it gets you your servlet.

   The upshot of this is that you want to design each of your servlets
   to service "root routes", and then use web.xml to "mount" them at
   different places in your website's namespace.  This is in contrast
   to plain ol' routing outside of servlets.  If you were running your
   code as a set of non-servlet handlers, you would include the
   complete path in your route definitions.
