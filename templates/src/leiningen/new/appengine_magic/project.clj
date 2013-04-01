(defproject {{appname}} "{{version}}"
  :description "FIXME: write description"
  :min-lein-version "2.0"
  :url {{proj-url}}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repl-options {:port 4005
                 :init (do
                         (require '[appengine-magic.core :as ae])
{{#servlets}}
                         (load-file "src/{{name}}/{{class}}.clj")
                         (defn {{class}} []
                           (do (load-file "src/{{name}}/{{class}}.clj")
                               (ae/serve {{name}}.{{class}}/{{name}}-{{class}})))
{{/servlets}}
                         (user))}
  :gae-sdk "{{sdk}}"
  :gae-app {:id "{{gae-app-id}}"
            ;; using '-' prefix on version nbr forces user to customize
            :version  {:dev "-{{gae-app-version}}"
                       :test "-{{gae-app-version}}"
                       :prod "-{{gae-app-version}}"}
            :servlets [{{#servlets}}{:name "{{name}}", :class "{{class}}",
                       :services [{{#services}}{:svcname "{{svcname}}" :url-pattern  "{{url-pattern}}"}
                                  {{/services}}]}
                       {{/servlets}}]
            :war "{{war}}"
            :display-name "{{display-name}}"
            :welcome "{{welcome}}"
            :threads {{threads}},
            :sessions {{sessions}},
            :java-logging "{{java-logging}}",
            ;; static-files: html, css, js, etc.
            :statics {:src "src/main/public"
                      :dest ""
                      :include {:pattern "public/**"
                                ;; :expire "5d"
                                }
                      ;; :exclude {:pattern "foo/**"}
                      }
            ;; resources: img, etc. - use lein default
            :resources {:src "src/main/resource"
                        :dest ""
                        :include {:pattern "public/**"
                                  ;; :expire "5d"
                                  }
                        ;; :exclude {:pattern "bar/**"}
                        }
            }
  :aot [{{#aots}}{{aot}} {{/aots}} *]
  :compile-path "{{war}}/WEB-INF/classes"
  :target-path "{{war}}/WEB-INF/lib"
  :keep-non-project-classes false
  :omit-source true ;; default
  :jar-exclusions [#"^WEB-INF/appengine-generated.*$"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0-beta1"]
                 [ring/ring-devel "1.2.0-beta1"]
                 [hiccup "1.0.2"]
                 [org.clojure/tools.logging "0.2.3"]]
  :profiles {:dev {:dependencies [[appengine-magic "0.5.1-SNAPSHOT"]]}}
  :plugins [[gaem "0.3.0-SNAPSHOT"]])
