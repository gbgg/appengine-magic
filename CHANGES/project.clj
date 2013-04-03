(defproject lex "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :min-lein-version "2.0"
  :url http://example.com/FIXME
  :java-source-paths ["src/main/java"]
  :javac-options ["-nowarn" "-target" "1.7" "-source" "1.6" "-Xlint:-options"]
  :resource-paths ["war/WEB-INF/classes/"
                   "war/WEB-INF/lib/*"
;;                   "war/WEB-INF/sdk/lib/*"
;; definitely not "war/WEB-INF/sdk/lib/impl/*"
;;                   "war/WEB-INF/sdk/lib/user/*"
                   ;; "war/WEB-INF/sdk/*"
                   ;; "war/WEB-INF/sdk/*"
                   ]
  :jvm-opts ["-javaagent:war/WEB-INF/sdk/lib/agent/appengine-agent.jar"
             "-Xbootclasspath/p:war/WEB-INF/lib/appengine-dev-jdk-overrides.jar"
             "-Ddatastore.auto_id_allocation_policy=scattered"
             "-Dappengine.sdk.root=war/WEB-INF/sdk"
             "-D--property=kickstart.user.dir=/Users/gar/private/sib/web/sibawayhi/lex.magic/"
             "-D--enable_all_permissions=true"
             "-Djava.awt.headless=true"]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repl-options {:port 4005
}                 ;; :init (do
                 ;;         (require '[appengine-magic.core :as ae])
                 ;;         (load-file "src/lex/request.clj")
                 ;;         (load-file "src/lex/user.clj")
                 ;;         (load-file "src/lex/views/layout.clj")
                 ;;         (load-file "src/lex/core.clj")
                 ;;         (defn request []
                 ;;           (do (load-file "src/lex/request.clj")
                 ;;               (ae/serve lex.request/lex-request)))
                 ;;         (defn user []
                 ;;           (do (load-file "src/lex/user.clj")
                 ;;               (ae/serve lex.user/lex-user)))
                 ;;         (defn core []
                 ;;           (do (load-file "src/lex/core.clj")
                 ;;               (ae/serve lex.core/lex-core)))
                 ;;         (user))}
  :gae-sdk "/usr/local/java/appengine"
  :gae-app {:id "arabiclexicon"
            ;; using '-' prefix on version nbr forces user to customize
            :version  {:dev "0-10-0"
                       :test "-0-1-0"
                       :prod "-0-1-0"}
            :servlets [{:name "lex", :class "request",
                       :services [{:svcname "request" :url-pattern  "/request/*"}
                                  ]}
                       {:name "lex", :class "core",
                        :services [{:svcname "lex"
                                    :url-pattern  "/pi/*"}
                                  ]}
                       {:name "lex", :class "core",
                        :services [{:svcname "lex"
                                    :url-pattern  "/root/*"}
                                  ]}
                       {:name "lex", :class "user",
                        :services [{:svcname "user" :url-pattern  "/user/*"}
                                  {:svcname "login" :url-pattern  "/_ah/login_required"}
                                  {:svcname "logout" :url-pattern  "/logout"}
                                  ]}
                       ]
            :war "war"
            :display-name "lex"
            :welcome "index.html"
            :threads true,
            :sessions true,
            :java-logging "logging.properties",
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
  :aot [lex.core lex.request lex.user  lex.views.layout]
;;  :source-paths ["src/lex"]
  :compile-path "war/WEB-INF/classes"
  :target-path "war/WEB-INF/lib"
;;  :uberjar-exclusions [#"META-INF/MANIFEST.MF"]
  :keep-non-project-classes false
  :omit-source true ;; default
  :jar-exclusions [#"^WEB-INF/appengine-generated.*$"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring "1.1.8"]
                 ;; [ring/ring-devel "1.2.0-beta1"]
                 ;; [ring/ring-codec "1.0.0"]
                 [hiccup "1.0.2"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [org.clojure/math.combinatorics "0.0.3"]
                 [org.apache.commons/commons-lang3 "3.1"]
;;                 [tasrifa "0.1.0-SNAPSHOT"]
                 [org.clojure/tools.logging "0.2.3"]]
  :profiles {:nodeps {:dependencies []}
             :dev {:dependencies [[appengine-magic "0.5.2-SNAPSHOT"]
;                                  [commons-codec "1.7"]
;                                  [commons-codec "20041127.091804"]
;                                  [com.google.appengine/appengine-testing "1.7.6"]
;;                                  [com.google.appengine/appengine-jsr107 "1.7.6"]
;;                                  [com.google.appengine/jsr107 "1.7.6"]
                                 [com.google.appengine/appengine-local-runtime "1.7.6"] ;; required for javac
;;                                  [com.google.appengine/appengine-local-runtime-shared "1.7.6"]
                                  ]}
             :repl {:dependencies [[appengine-magic "0.5.2-SNAPSHOT"]]}}
;;  :eval-in-leiningen true
  :plugins [[gaem "0.2.0-SNAPSHOT"]])
