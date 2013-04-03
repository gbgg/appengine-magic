(ns leiningen.magic.jetty
  "jetty - run a jetty server without servlet container services"
  (:import com.google.appengine.tools.KickStart
           com.google.appengine.tools.development.DevAppServerMain)
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn jetty
  "jetty"
  [& args]
  (println "running jetty...(not implemented yet)"))
