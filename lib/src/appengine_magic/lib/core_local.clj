(in-ns 'appengine-magic.lib.core)

(use 'appengine-magic.lib.local-env-helpers
     '[appengine-magic.lib.servlet :only [servlet]]
     '[appengine-magic.lib.swank :only [wrap-swank]]
     '[ring.middleware.file :only [wrap-file]]
     '[ring.middleware.file-info :only [wrap-file-info]])

(require '[clojure.string :as str]
         '[appengine-magic.lib.jetty :as jetty]
         '[appengine-magic.lib.blobstore-upload :as blobstore-upload])

(import java.io.File
        com.google.apphosting.api.ApiProxy)



;;; ----------------------------------------------------------------------------
;;; appengine-magic core API functions
;;; ----------------------------------------------------------------------------

(defn default-war-root []
  (-> (clojure.lang.RT/baseLoader)
      (.getResource ".")
      .getFile
      java.net.URLDecoder/decode
      (File. "../war")
      .getAbsolutePath))


(defn appengine-base-url [& {:keys [https?] :or {https? false}}]
  ;; NB: The https? argument is intentionally ignored. HTTPS is not supported
  ;; for local environments.
  (str "http://localhost:"
       (str @appengine-magic.lib.local-env-helpers/*current-server-port*)))


(defn wrap-war-static [app, #^String war-root]
  (fn [req]
    (let [#^String uri (:uri req)]
      (if (.startsWith uri "/WEB-INF")
          (app req)
          ((wrap-file-info (wrap-file app war-root)) req)))))


(defmacro def-appengine-app [app-var-name handler & {:keys [war-root]}]
  `(def ~app-var-name
        (let [handler# ~handler
              war-root-arg# ~war-root
              war-root# (if (nil? war-root-arg#)
                            (default-war-root)
                            war-root-arg#)]
          {:handler (-> handler#
                        wrap-swank
                        (wrap-war-static war-root#))
           :war-root war-root#})))


(defn make-appengine-request-environment-filter []
  (reify javax.servlet.Filter
    (init [_ filter-config]
      (.setAttribute (.getServletContext filter-config)
                     "com.google.appengine.devappserver.ApiProxyLocal"
                     (ApiProxy/getDelegate)))
    (destroy [_])
    (doFilter [_ req resp chain]
      (let [all-cookies (.getCookies req)
            login-cookie (when all-cookies
                           (let [raw (first (filter #(= "dev_appserver_login" (.getName %))
                                                    (.getCookies req)))]
                             (when raw (.getValue raw))))
            [user-email user-admin? _] (when login-cookie
                                         (str/split login-cookie #":"))
            thread-environment-proxy (make-thread-environment-proxy :user-email user-email
                                                                    :user-admin? user-admin?)]
        (ApiProxy/setEnvironmentForCurrentThread thread-environment-proxy))
      (.doFilter chain req resp))))



