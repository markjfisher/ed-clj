(ns ed.schema
  (:require [ed.errors :refer [fail attempt-all]]
            [schema.core :as s]))

(def FactionSchema
  "A schema for factions."
  {(s/required-key :id)                s/Int
   (s/required-key :name)              s/Str
   (s/required-key :updated-at)        s/Int
   (s/required-key :government-id)     s/Int
   (s/required-key :government)        s/Str
   (s/required-key :allegiance-id)     s/Int
   (s/required-key :allegiance)        s/Str
   (s/required-key :home-system-id)    s/Int
   (s/required-key :is-player-faction) s/Bool
   (s/required-key :state-id)          s/Int
   (s/required-key :state)             s/Str})

(def SystemFactionSchema
  "A schema for the list of factions in a system."
  {(s/required-key :minor-faction-id) s/Int
   (s/required-key :state-id)         s/Int
   (s/required-key :influence)        s/Num
   (s/required-key :state)            s/Str})

(def SystemDataSchema
  "A schema for Systems."
  {(s/required-key :id)                           s/Int
   (s/required-key :updated-at)                   s/Int
   (s/required-key :edsm-id)                      s/Int
   (s/required-key :name)                         s/Str
   (s/required-key :x)                            s/Num
   (s/required-key :y)                            s/Num
   (s/required-key :z)                            s/Num
   (s/required-key :population)                   s/Int
   (s/required-key :is-populated)                 s/Bool
   (s/required-key :government-id)                s/Int
   (s/required-key :government)                   s/Str
   (s/required-key :allegiance-id)                s/Int
   (s/required-key :allegiance)                   s/Str
   (s/required-key :state-id)                     s/Int
   (s/required-key :state)                        s/Str
   (s/required-key :security-id)                  s/Int
   (s/required-key :security)                     s/Str
   (s/required-key :primary-economy-id)           s/Int
   (s/required-key :primary-economy)              s/Str
   (s/required-key :power)                        s/Str
   (s/required-key :power-state)                  s/Str
   (s/required-key :power-state-id)               s/Int
   (s/required-key :needs-permit)                 s/Bool
   (s/required-key :simbad-ref)                   s/Str
   (s/required-key :controlling-minor-faction-id) s/Int
   (s/required-key :controlling-minor-faction)    s/Str
   (s/required-key :reserve-type-id)              s/Int
   (s/required-key :reserve-type)                 s/Str
   (s/required-key :minor-faction-presences)      [SystemFactionSchema]})

(defn validate
  "Wrap schema's validate so we can return a fail instead of exception."
  [t i]
  (try
    (s/validate t i)
    (catch Exception e
      (fail (.getMessage e)))))
