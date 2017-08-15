(ns ed.pulsar-journal-events
  (:gen-class)
  (:require [ed.db :as d]
            [ed.errors :refer [attempt-all fail]]
            [ed.schema :as s]
            [ed.util :as u]
            [ed.loaders :as l]
            [cheshire.core :as jc]
            [co.paralleluniverse.pulsar.actors :refer [! !! actor receive register! spawn whereis]]
            [co.paralleluniverse.pulsar.core :refer [defsfn join]]
            [taoensso.timbre :as timbre :refer [info error infof errorf]]
            [clojure.string :as str]))

(defsfn update-faction-from-fsdjump!
  "input:
  jump-data: {... :Factions [{ :Allegiance \"Federation\" :FactionState \"CivilWar\" :Influence 0.0412 :Name \"FooSystem\" :Government \"Corporate\" } ... ]
  system: {:id 1 ... :minor-faction-presences [{:minor-faction-id 500 :state-id 1 :influence 4.56 :state \"Civil War\"}]
  "
  [jump-data system]
  (let [foo :foo]
    )
  )

(defsfn update-system-from-fsdjump!
  [jump-data system]
  (let [current-controlling-faction (d/get-faction (:controlling-minor-faction-id system))
        new-controlling-faction (d/get-faction-by-name {:name (:SystemFaction data)})
        current-state-id (:state-id system)
        new-state-id (l/lookup :state (:FactionState jump-data))
        current-allegiance-id (:allegiance-id system)
        new-allegiance-id (l/lookup :allegiance (:SystemAllegiance jump-data))
        event-time (u/timestamp-to-int (:timestamp jump-data))
        now (u/now-as-int)]
    (when (and (> (+ now 600) event-time)
               (or (not= new-controlling-faction current-controlling-faction)
                   (not= new-state-id current-state-id)
                   (not= new-allegiance-id current-allegiance-id)))
      (d/update-system-from-fsdjump!
        (:id system)
        new-controlling-faction
        new-state-id
        new-allegiance-id
        event-time))))

(defsfn add-fsd-jump-data!
  "Adds fsd jump data to datastore"
  [jump-data system]
  (update-system-from-fsdjump! jump-data system)
  (update-faction-from-fsdjump! jump-data system))

(defsfn get-system-within-1ly
  "Matches a system by name and within 1 ly of the jump data given.
  This allows us to find systems that have duplicate entries by name.
  The errors are due to precision in the FSD Jump data being lower than the system data."
  [data]
  (let [systems (d/get-system-by-name {:name (:StarSystem data)})
        star-pos (:StarPos data)]
    (first (filter (fn [s]
                     (> 1 (u/distance-between (zipmap [:x :y :z] star-pos)
                                              (select-keys s [:x :y :z]))))
                   systems))))

(defsfn process-fsd-jump-event
  [data]
  (attempt-all err [jump-data (s/validate-journal-fsd-jump data)
                    system (get-system-within-1ly jump-data)]
               (if system
                 (add-fsd-jump-data! jump-data system)
                 (errorf "No known system found within 1 ly of fsd-jump data: %s" data))
               (errorf "Failed to process fsd-jump data: %s\nError: %s" data (:message err))))

(defsfn journal-actor
  "Worker to process journal entries"
  []
  (loop []
    (receive
      [m]

      [:fsd-jump data]
      (process-fsd-jump-event data)

      ;; other journal entries here...

      ;; catch all
      :else
      (errorf "Unknown message received in journal-actor - %s" m))
    (recur)))

(defsfn journal-handler
  "A lightweight actor that sends messages onto worker actors depending on message"
  [actors]
  (actor [as (atom actors)]
         (loop []
           (receive [m]
                    [:fsd-jump data]
                    (let [sa (first @as)]
                      (! sa [:fsd-jump data])
                      (reset! as (u/rotate 1 @as)))

                    :else
                    (errorf "Unknown message received in journal-handler - %s" m))
           (recur))))

(defn create-journal-handler
  "Create a journal handler with n journal actors"
  [n]
  (let [sa (fn [] (spawn journal-actor))
        sas (take n (repeatedly sa))
        handler (spawn :name :journal-handler (journal-handler sas))]
    handler))

(defn start-journal-handlers
  "Creates and starts handlers to process different journal entries"
  [n]
  (when-not (whereis :journal-handler 2000 :ms)
    (register! (create-journal-handler n))
    (infof "Spawned :journal-handler with %d actors" n)))
