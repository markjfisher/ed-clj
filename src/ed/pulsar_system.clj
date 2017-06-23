(ns ed.pulsar-system
  (:gen-class)
  (:require [ed.db :as d]
            [ed.errors :refer [attempt-all fail]]
            [ed.schema :as s]
            [ed.util :as u]
            [cheshire.core :as jc]
            [co.paralleluniverse.pulsar.actors :refer [! !! actor receive register! spawn whereis]]
            [co.paralleluniverse.pulsar.core :refer [defsfn join]]
            [taoensso.timbre :as timbre :refer [info error infof errorf]]))

(defn create-system-map
  [m]
  (select-keys m
               [:id :updated-at :edsm-id :name :x :y :z :population
                :is-populated :government-id :allegiance-id :state-id
                :security-id :primary-economy-id :power :power-state-id
                :needs-permit :simbad-ref :controlling-minor-faction-id
                :reserve-type-id]))

(defsfn add-json-system
  [data]
  (attempt-all err [system-data (s/validate-system data)
                    old-system-row (d/get-system {:id (:id system-data)})]
               (if (and (some? old-system-row)
                        (> (:updated-at system-data) (:updated_at old-system-row))
                        (or (not= (:edsm-id system-data) (:edsm_id old-system-row))
                            (not= (:name system-data) (:name old-system-row))
                            (not= (:x system-data) (:x old-system-row))
                            (not= (:y system-data) (:y old-system-row))
                            (not= (:z system-data) (:z old-system-row))
                            (not= (:population system-data) (:population old-system-row))
                            (not= (:is-populated system-data) (:is_populated old-system-row))
                            (not= (:government-id system-data) (:government_id old-system-row))
                            (not= (:allegiance-id system-data) (:allegiance_id old-system-row))
                            (not= (:state-id system-data) (:state_id old-system-row))
                            (not= (:security-id system-data) (:security_id old-system-row))
                            (not= (:primary-economy-id system-data) (:primary_economy_id old-system-row))
                            (not= (:power system-data) (:power old-system-row))
                            (not= (:power-state-id system-data) (:power_state_id old-system-row))
                            (not= (:needs-permit system-data) (:needs_permit old-system-row))
                            (not= (:simbad-ref system-data) (:simbad_ref old-system-row))
                            (not= (:controlling-minor-system-id system-data) (:controlling_minor_system_id old-system-row))
                            (not= (:reserve-type-id system-data) (:reserve_type_id old-system-row))))
                 (d/update-system! (create-system-map system-data))
                 (when (nil? old-system-row)
                   (d/insert-system! (create-system-map system-data))))
               (errorf "Failed to load row from system data: %s\nError: %s" data (:message err))))

(defsfn system-actor []
  (loop []
    (receive
      [m]

      ;; validate and add the JSONL input and pass it on to :faction-update
      [:add-json-system data]
      (add-json-system data)

      ;; other data messages, e.g update from FSDJump

      ;; catch all
      :else
      (errorf "Unknown message received in system-actor - %s" m))
    (recur)))

(defsfn system-handler [actors]
  (actor [as (atom actors)]
         (loop []
           (receive [m]
                    [:add-json-system data]
                    (let [sa (first @as)]
                      (! sa [:add-json-system data])
                      (reset! as (u/rotate 1 @as)))

                    :else
                    (errorf "Unknown message received in system-handler - %s" m))
           (recur))))

(defn create-system-handler
  "Create a system handler with n system actors"
  [n]
  (let [sa (fn [] (spawn system-actor))
        sas (take n (repeatedly sa))
        handler (spawn :name :system-handler (system-handler sas))]
    handler))

(defn start-system-handler
  "Creates and starts a system actor to process jsonl for system and system changes"
  [n]
  (when-not (whereis :system-handler 2000 :ms)
    (register! (create-system-handler n))
    (infof "Spawned :system-handler with %d actors" n))
  (whereis :system-handler))
