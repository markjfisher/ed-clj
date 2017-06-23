(ns ed.pulsar-faction
  (:gen-class)
  (:require [ed.db :as d]
            [ed.errors :refer [attempt-all fail]]
            [ed.schema :as s]
            [ed.util :as u]
            [cheshire.core :as jc]
            [co.paralleluniverse.pulsar.actors :refer [! actor receive register! spawn whereis]]
            [co.paralleluniverse.pulsar.core :refer [defsfn join]]
            [taoensso.timbre :as timbre :refer [info error infof errorf]]))

(defn create-faction-map
  [m]
  (select-keys m
               [:id :name :updated-at :government-id
                :allegiance-id :state-id :home-system-id
                :is-player-faction]))

(defsfn add-json-faction
  [data]
  (attempt-all err [faction-data (s/validate-faction data)
                    old-faction-row (d/get-faction (select-keys faction-data [:id]))]
               (if (and (some? old-faction-row)
                        (> (:updated-at faction-data) (:updated_at old-faction-row))
                        (or (not= (:name faction-data) (:name old-faction-row))
                            (not= (:government-id faction-data) (:government_id old-faction-row))
                            (not= (:allegiance-id faction-data) (:allegiance_id old-faction-row))
                            (not= (:state-id faction-data) (:state_id old-faction-row))
                            (not= (:home-faction-id faction-data) (:home_faction_id old-faction-row))
                            (not= (:is-player-faction faction-data) (:is_player_faction old-faction-row))))
                 (d/update-faction! (create-faction-map faction-data))
                 (when (nil? old-faction-row)
                   (d/insert-faction! (create-faction-map faction-data))))
               (errorf "Failed to process faction data: %s\nError: %s" data (:message err))))

(defsfn faction-actor []
  (loop []
    (receive
      [m]

      ;; validate and add the JSONL input and pass it on to :faction-update
      [:add-json-faction data]
      (add-json-faction data)

      ;; other data messages, e.g update from FSDJump

      ;; catch all
      :else
      (errorf "Unknown message received in :faction-actor - %s" m))
    (recur)))

(defsfn faction-handler [actors]
  (actor [as (atom actors)]
         (loop []
           (receive [m]
                    [:add-json-faction data]
                    (let [fa (first @as)]
                      (! fa [:add-json-faction data])
                      (reset! as (u/rotate 1 @as)))

                    :else
                    (errorf "Unknown message received in :faction-handler - %s" m))
           (recur))))

(defn create-faction-handler
  "Create a faction handler with n faction actors"
  [n]
  (let [fa (fn [] (spawn faction-actor))
        fas (take n (repeatedly fa))
        handler (spawn :name :faction-handler (faction-handler fas))]
    handler))

(defn start-faction-handler
  "Creates and starts a faction actor to process jsonl for faction and faction changes"
  [n]
  (when-not (whereis :faction-handler 2000 :ms)
    (register! (create-faction-handler n))
    (infof "Spawned :faction-handler with %d actors" n))
  (whereis :faction-handler))
