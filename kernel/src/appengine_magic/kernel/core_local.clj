(in-ns 'appengine-magic.lib)

(use 'appengine-magic.kernel.local-env-helpers
     '[appengine-magic.kernel.servlet :only [servlet]]
     '[appengine-magic.kernel.swank :only [wrap-swank]]
     '[ring.middleware.file :only [wrap-file]]
     '[ring.middleware.file-info :only [wrap-file-info]])

(require '[clojure.string :as str]
         '[appengine-magic.kernel.jetty :as jetty]
         '[appengine-magic.kernel.blobstore-upload :as blobstore-upload])

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




