(ns ed.conf
  (:gen-class)
  (:require [ed.util :as u]
            [mount.core :refer [defstate]]))

(defstate config
          :start (u/load-config "config.edn"))
