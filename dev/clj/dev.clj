(ns dev
  (:require [mount.core :refer [start stop]]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]

            [clojure.pprint :refer [pprint]]
            [mount.core :as mount :refer [defstate]]
            [mount.tools.graph :refer [states-with-deps]]

            [ed.db :refer [config *db*]]
            #_[app.example]
            ;; <<<< replace this your "app" namespace(s) you want to be available at REPL time
            #_[app.nyse :refer [find-orders add-order]]

            ))


(defn start []
  #_(with-logging-status)
  (mount/start #'app.conf/config
               #'app.db/conn
               #'app.www/nyse-app
               #'app.example/nrepl))             ;; example on how to start app with certain states

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