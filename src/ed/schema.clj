(ns ed.schema
  (:gen-class)
  (:require [ed.errors :refer [fail attempt-all]]
            [ed.util :as u]
            [cheshire.core :as jc]
            [schema.core :as s]))

(def FactionSchema
  "A schema for factions."
  {(s/required-key :id)                s/Int
   (s/required-key :name)              s/Str
   (s/required-key :updated-at)        s/Int
   (s/required-key :government-id)     (s/maybe s/Int)
   (s/required-key :government)        (s/maybe s/Str)
   (s/required-key :allegiance-id)     (s/maybe s/Int)
   (s/required-key :allegiance)        (s/maybe s/Str)
   (s/required-key :home-system-id)    (s/maybe s/Int)
   (s/required-key :is-player-faction) (s/maybe s/Bool)
   (s/required-key :state-id)          (s/maybe s/Int)
   (s/required-key :state)             (s/maybe s/Str)})

(def SystemFactionSchema
  "A schema for the list of factions in a system."
  {(s/required-key :minor-faction-id) s/Int
   (s/required-key :state-id)         (s/maybe s/Int)
   (s/required-key :influence)        (s/maybe s/Num)
   (s/required-key :state)            (s/maybe s/Str)})

(def SystemDataSchema
  "A schema for Systems."
  {(s/required-key :id)                           s/Int
   (s/required-key :updated-at)                   s/Int
   (s/required-key :edsm-id)                      (s/maybe s/Int)
   (s/required-key :name)                         s/Str
   (s/required-key :x)                            s/Num
   (s/required-key :y)                            s/Num
   (s/required-key :z)                            s/Num
   (s/required-key :population)                   (s/maybe s/Int)
   (s/required-key :is-populated)                 (s/maybe s/Bool)
   (s/required-key :government-id)                (s/maybe s/Int)
   (s/required-key :government)                   (s/maybe s/Str)
   (s/required-key :allegiance-id)                (s/maybe s/Int)
   (s/required-key :allegiance)                   (s/maybe s/Str)
   (s/required-key :state-id)                     (s/maybe s/Int)
   (s/required-key :state)                        (s/maybe s/Str)
   (s/required-key :security-id)                  (s/maybe s/Int)
   (s/required-key :security)                     (s/maybe s/Str)
   (s/required-key :primary-economy-id)           (s/maybe s/Int)
   (s/required-key :primary-economy)              (s/maybe s/Str)
   (s/required-key :power)                        (s/maybe s/Str)
   (s/required-key :power-state)                  (s/maybe s/Str)
   (s/required-key :power-state-id)               (s/maybe s/Int)
   (s/required-key :needs-permit)                 (s/maybe s/Bool)
   (s/required-key :simbad-ref)                   (s/maybe s/Str)
   (s/required-key :controlling-minor-faction-id) (s/maybe s/Int)
   (s/required-key :controlling-minor-faction)    (s/maybe s/Str)
   (s/required-key :reserve-type-id)              (s/maybe s/Int)
   (s/required-key :reserve-type)                 (s/maybe s/Str)
   (s/required-key :minor-faction-presences)      (s/maybe [SystemFactionSchema])})

(defn validate
  "Wrap schema's validate so we can return a fail instead of exception."
  [t i]
  (try
    (s/validate t i)
    (catch Exception e
      (fail (.getMessage e)))))

(defn validate-system
  "Validate system from json string"
  [l]
  (attempt-all err [system-all (jc/parse-string l u/name-to-keyword)
                    system (validate SystemDataSchema system-all)]
               system
               (fail (format "Failed to parse system data %s:\n%s" l (:message err)))))

(defn validate-faction
  "Validate faction data from json string."
  [l]
  (attempt-all err [faction-all (jc/parse-string l u/name-to-keyword)
                    f-data (validate FactionSchema faction-all)]
               f-data
               (fail (format "Failed to parse faction data %s:\n%s" l (:message err)))))