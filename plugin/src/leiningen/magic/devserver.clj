(ns leiningen.magic.devserver
  "devserver - run the local official GAE DevAppServer in a repl"
  (:import com.google.appengine.tools.KickStart
           com.google.appengine.tools.development.DevAppServerMain)
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn devserver
  "devserver"
  [& args]
  (println "running devserver...(not implemented yet)"))
