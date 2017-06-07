(ns ed.server
  (:require [ed.conf :refer [config]]
            [ed.db :as d]
            [mount.core :as mount :refer [defstate]]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [taoensso.timbre :as timbre :refer [info infof]])
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
                     (infof "Starting nrepl service on port %d" (:port nrepl-config))
                     (start-nrepl nrepl-config)))
          :stop (let [nrepl-config (:nrepl config)
                      nrepl-port (or (:port nrepl-config) 0)]
                  (when (> nrepl-port 0)
                    (stop-server nrepl))))

;; this is called by calling the alias "lein godev"
;; or directly "lein run -m ed.server"
(defn -main
  ([]
   (mount/start)
   (let [timbre-config (:timbre config)]
     (timbre/merge-config! timbre-config)
     (infof "timbre-config: %s" timbre-config))

   (info "starting main")
   (d/drop-tables!)
   (d/init-db!)

   (info "... -main complete"))
  ([_]
    ;; just stops IDE from showing unused main
   (-main)))
