(ns ed.server
  (:require [ed.conf :refer [config]]
            [ed.db :as d]
            [mount.core :as mount :refer [defstate]]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [taoensso.timbre :as timbre :refer [info infof report reportf]])
  (:import (java.util TimeZone))
  (:gen-class))


(defn- start-nrepl [{:keys [host port]}]
  (start-server :bind host :port port))

;; you can specifically stop and start this with:
;; (mount/start #'ed.server/nrepl)
;; (mount/stop #'ed.server/nrepl)
(defstate nrepl
          ;; only create the nrepl if the given port is > 0, so it isn't started in 'dev' mode (which is intellij nrepl)
          :start (let [nrepl-config (:nrepl config)
                       nrepl-port (or (:port nrepl-config) 0)]
                   (when (> nrepl-port 0)
                     (reportf "Starting nrepl service on port %d" (:port nrepl-config))
                     (start-nrepl nrepl-config)))
          :stop (let [nrepl-config (:nrepl config)
                      nrepl-port (or (:port nrepl-config) 0)]
                  (when (> nrepl-port 0)
                    (stop-server nrepl))))

;; this is called by calling the alias "lein godev"
;; or directly "lein run -m ed.server"
(defn -main
  ([]
    ;; get the config values only so we can set logging level
    ;; and stop the d/b pool from being too verbose.
   (mount/start #'ed.conf/config)
   (timbre/merge-config! (:timbre config))

    ;; this loads all other state, including the pool, but logging will be honoured
   (mount/start)

   (info "starting main")
   (d/init-db!)

   (report "-main complete."))
  ([_]
    ;; just stops IDE from showing unused main
   (-main)))
