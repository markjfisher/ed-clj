(ns ed.conf
  (:require [ed.util :as u]
            [mount.core :refer [defstate]]))

(defstate config
          :start (u/load-config "config.edn"))
