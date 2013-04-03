(in-ns 'appengine-magic.lib.core)

(import '[java.io File FileInputStream BufferedInputStream])

(defmacro def-appengine-app [app-var-name handler & [args]]
  `(def ~app-var-name ~handler))
