(ns ed.server
  (:require [ed.conf :refer [config]]
            [ed.pulsar-faction :as pf]
            [ed.pulsar-system :as ps]
            [ed.pulsar-journal-events :as pj]
            [ed.db :as d]
            [ed.loaders :as l]
            [mount.core :as mc]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [co.paralleluniverse.pulsar.core :refer [join]]
            [taoensso.timbre :as timbre :refer [info infof report reportf]])
  (:import (java.util TimeZone))
  (:gen-class))


(defn- start-nrepl [{:keys [host port]}]
  (start-server :bind host :port port))

;; you can specifically stop and start this with:
;; (mount/start #'ed.server/nrepl)
;; (mount/stop #'ed.server/nrepl)
(mc/defstate nrepl
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
   (mc/start #'ed.conf/config)
   (timbre/merge-config! (:timbre config))

    ;; this loads all other state, including the pool, but logging will be honoured
   (mc/start)

   (info "starting main")
   (let [fac (get-in config [:app :faction-actor-count] 20)
         sac (get-in config [:app :system-actor-count] 20)
         jac (get-in config [:app :journal-actor-count] 20)
         fh (pf/start-faction-handler fac)
         sh (ps/start-system-handler sac)]
     (d/init-db!)
     (l/load-data!)
     (pj/start-journal-handlers jac)
     (report "-main complete. joining actors to cause pause...")
     (join fh)
     (report "... joined never prints!")))
  ([_]
    ;; just stops IDE from showing unused main
   (-main)))
