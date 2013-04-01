(ns leiningen.magic
  "magic - a leiningen plugin for configuring and deploying appengine-magic apps.

To create a new appengine-magic app, use the standard leiningen 'new' task
with the magic template, like so:

    $ lein new appengine-magic <myproj>:<gae-app-id> /path/to-sdk

Once you have created the app, you can use standard leiningen 'compile',
'jar', 'uberjar' to deal with your clojure code.  They use the
standard :target-path to decide where to put the jar.  This plugin also supports subtasks clean, config, delein, and deploy."
  (:import java.io.File
           ;; com.google.appengine.tools.admin.AppCfg
           ;; [org.apache.commons.exec CommandLine DefaultExecutor]
           )
  (:require [leiningen.magic.clean :as clean]
            [leiningen.magic.config :as config]
            [leiningen.magic.deploy :as deploy]
            [leiningen.magic.dev_appserver :as dev_appserver]
            [leiningen.magic.delein :as delein]
            [leiningen.magic.util :as util]
            [clojure.tools.cli :as cli]
            [clojure.tools.logging :as log :only [debug info]]))

  ;; (:use [leiningen.new.templates :only [project-name
  ;;                                       renderer multi-segment
  ;;                                       sanitize-ns name-to-path
  ;;                                       year ->files]]))

(try ; Leiningen 2.0 compatibility
  (use '[leiningen.core :only [abort]])
  (catch Exception _
    (use '[leiningen.core.main :only [abort]])))

(defn magic-test [project & args]
  (println "ok"))

;; cribbed from heroku plugin:

(def ^{:private true} cli-options
  ["-a" "--app" "App to use if not in project dir."])

(defn- task-not-found [& _]
  (abort "That's not a task. Use \"lein magic help\" to list all subtasks."))

;; like leiningen.core, but with special subtask handling for colon-grouping
(defn- resolve-task
  ([task not-found]
     (let [task-ns (symbol (str "leiningen.magic." (first (.split task ":"))))
           task (symbol task)]
       (try
         (when-not (find-ns task-ns)
           (require task-ns))
         (or (ns-resolve task-ns task)
             not-found)
         (catch java.io.FileNotFoundException e
           not-found))))
  ([task] (resolve-task task #'task-not-found)))

(defn #^{:subtasks [#'clean/clean #'config/config
                    #'delein/delein
                    #'dev_appserver/dev_appserver
                    #'deploy/deploy]} 
  magic
  "manage appengine-magic app"
  [project cmd & args]
  (let [subtask (resolve-task (or cmd "help"))
        command cmd]
    (try
      (binding [util/*app* nil] ;;(:app opts)]
        (apply subtask project args))
      (catch Exception e
        (abort (or (.getMessage e) (pr e)))))))

;;   [project & args]
;;   (let [parms (map #(read-string %) args)]
;;     (cond
;;      (some #{:config} parms) (config project args)
;;      (some #{:deploy} parms) (deploy/deploy project args)
;;      (some #{:test} parms)    (magic-test project args)
;;       :else (println "Usage: lein magic <subtask>
;;     subtasks: :config, :deploy
;; See 'lein help magic'."))))


