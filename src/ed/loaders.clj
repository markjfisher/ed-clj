(ns ed.loaders
  (:require [ed.schema :as s]
            [ed.util :as u]
            [ed.errors :refer [attempt-all fail]]
            [cheshire.core :as jc]))

(defn validate-faction
  "Given a json string for a faction, validate it and return a list of maps for faction and faction-state."
  [l]
  (attempt-all err [faction-all (jc/parse-string l u/name-to-keyword)
                    f-no-state (dissoc faction-all :state :state-id)
                    f-state (assoc (select-keys faction-all [:state :state-id])
                              :id (:id faction-all)
                              :updated-at (:updated-at faction-all))
                    f-data (s/validate s/FactionSchema f-no-state)
                    fs-data (s/validate s/FactionStateSchema f-state)]
               [f-data fs-data]
               (fail (format "Failed to parse faction data %s:\n%s" l (:message err)))))

