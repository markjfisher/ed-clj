(ns dev
  (:require [mount.core :as mount]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]

            [clojure.pprint :refer [pprint]]
            [mount.core :as mount :refer [defstate]]
            [mount.tools.graph :refer [states-with-deps]]

            [ed.errors]
            [ed.conf :refer [config]]
            [ed.db :refer [*db*]]
            [ed.server :refer [nrepl]]
            [ed.schema]
            [ed.util]
            [ed.zip]
            [ed.zmq]
            ))

;; don't start the nrepl service as we're probably running in intellij repl
(defn start []
  (mount/start #'ed.conf/config
               #'ed.db/*db*))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (tn/refresh :after 'dev/go))

(mount/in-clj-mode)