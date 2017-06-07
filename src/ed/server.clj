(ns ed.server
  (:require [ed.conf :refer [config]]
            [ed.db :as d]
            [ed.util :as u]
            [taoensso.timbre :as timbre :refer (log info error logf infof errorf)]
            [mount.core :as mc]))

;; this is called by calling the alias "lein godev"
;; or directly "lein run -m ed.server"
(defn -main
  ([]
   (let [timbre-config (:timbre config)]
     (timbre/merge-config! timbre-config)
     (infof "timbre-config: %s" timbre-config))

   (info "starting main")
   (mc/start)
   (d/drop-tables!)
   (d/init-db!)

   (info "... -main complete"))
  ([_]
    ;; just stops IDE from showing unused main
   (-main)))
