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
               {:faction f-data :faction-state fs-data}
               (fail (format "Failed to parse faction data %s:\n%s" l (:message err)))))

(defn validate-system
  "Given a json string for the system, validate it and return a map of the data."
  [l]
  (attempt-all err [system-all (jc/parse-string l u/name-to-keyword)
                    system (s/validate s/SystemDataSchema system-all)]
               {:system system}
               (fail (format "Failed to parse system data %s:\n%s" l (:message err)))))
