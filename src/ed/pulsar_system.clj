;; this class handles system data from nightly eddb dumps
;; by spawning actors to process jsonl lines as they are read

(ns ed.pulsar-system
  (:gen-class)
  (:require [ed.db :as d]
            [ed.errors :refer [attempt-all fail]]
            [ed.schema :as s]
            [ed.util :as u]
            [cheshire.core :as jc]
            [co.paralleluniverse.pulsar.actors :refer [! !! actor receive register! spawn whereis]]
            [co.paralleluniverse.pulsar.core :refer [defsfn join]]
            [taoensso.timbre :as timbre :refer [info error infof errorf]]
            [clojure.string :as str]))

(defsfn create-system-map
  [m]
  (select-keys m
               [:id :updated-at :edsm-id :name :x :y :z :population
                :is-populated :government-id :allegiance-id :state-id
                :security-id :primary-economy-id :power :power-state-id
                :needs-permit :simbad-ref :controlling-minor-faction-id
                :reserve-type-id]))

(defsfn create-system-faction-map
  [m]
  (select-keys m [:system-id :updated-at :minor-faction-id :state-id :influence]))

(defsfn faction-data-changed?
  "Returns true if any faction information between the two maps have changed.
  The data is in the format:
  current: {             :minor-faction-id 1               :state-id 0 :influence 10.0 :state \"Civil War\"}
  new    : {:system_id 1 :minon_faction_id 1 :updated_at 1 :state_id 0 :influence 10.0}
  Thus, for each minor-faction-id and compare state-id and influence.
  Algorithm: create new maps of current and new just holding the values to compare with normalised names and compare."
  [current new]
  (let [mc1 (select-keys current [:minor-faction-id :state-id :influence])
        mn1 (select-keys new [:minor_faction_id :state_id :influence])
        mn2 (into {} (for [[k v] mn1] [(-> k name (str/replace "_" "-") keyword) v]))]
    (not= mc1 mn2)))

(defsfn find-entry-in-maps
  "Find the first map with k/v pair in a list of maps ms"
  [ms k v]
  (first (reduce #(if (= v (get %2 k))
                    (conj %1 %2)
                    %1)
                 []
                 ms)))

(defsfn any-faction-data-changed?
  "Returns true if any faction information between the two list of maps have changed.
  The parameters are a list of maps, of form:
  current: [{             :minor-faction-id 1               :state-id 0 :influence 10.0 :state \"Civil War\"}, ...]
  new    : [{:system_id 1 :minon_faction_id 1 :updated_at 1 :state_id 0 :influence 10.0}, ...].
  Here, we check the map sizes are equal, then test each matching map based on minor-faction-id"
  [current new]
  (if (= (count current) (count new))
    (let [c-minor-ids (map :minor-faction-id current)
          n-minor-ids (map :minor_faction_id new)
          diff-ids (not= (sort c-minor-ids) (sort n-minor-ids))
          change-seq (for [id c-minor-ids]
                       (let [c-faction (find-entry-in-maps current :minor-faction-id id)
                             n-faction (find-entry-in-maps new :minor_faction_id id)]
                         (faction-data-changed? c-faction n-faction)))]
      (or diff-ids (some true? change-seq) false))
    true))

(defsfn change-system!
  "Updates or Inserts the system and system faction data based on type of :insert or :update"
  [sys-data fac-data type]
  (let [fns {:update {:system d/update-system! :system-faction d/update-system-faction!}
             :insert {:system d/insert-system! :system-faction d/insert-system-faction!}}
        sys-fn (get-in fns [type :system])
        fac-fn (get-in fns [type :system-faction])]
    (sys-fn (create-system-map sys-data))
    (dorun (for [f fac-data]
             (fac-fn (create-system-faction-map
                     (merge f {:system-id (:id sys-data)
                               :updated-at (:updated-at sys-data)})))))))

(defsfn add-json-system
  [data]
  ;; TODO: ensure the updated-at is not after now, so we don't honour rogue future data
  (attempt-all err [new-system-data (s/validate-system data)
                    current-system-data (d/get-system {:id (:id new-system-data)})
                    new-faction-data (:minor-faction-presences new-system-data)
                    current-faction-data (d/get-system-faction {:system-id (:id new-system-data)})]
               (if (and (some? current-system-data)
                        (> (:updated-at new-system-data) (:updated_at current-system-data))
                        (or (not= (:edsm-id new-system-data) (:edsm_id current-system-data))
                            (not= (:name new-system-data) (:name current-system-data))
                            (not= (:x new-system-data) (:x current-system-data))
                            (not= (:y new-system-data) (:y current-system-data))
                            (not= (:z new-system-data) (:z current-system-data))
                            (not= (:population new-system-data) (:population current-system-data))
                            (not= (:is-populated new-system-data) (:is_populated current-system-data))
                            (not= (:government-id new-system-data) (:government_id current-system-data))
                            (not= (:allegiance-id new-system-data) (:allegiance_id current-system-data))
                            (not= (:state-id new-system-data) (:state_id current-system-data))
                            (not= (:security-id new-system-data) (:security_id current-system-data))
                            (not= (:primary-economy-id new-system-data) (:primary_economy_id current-system-data))
                            (not= (:power new-system-data) (:power current-system-data))
                            (not= (:power-state-id new-system-data) (:power_state_id current-system-data))
                            (not= (:needs-permit new-system-data) (:needs_permit current-system-data))
                            (not= (:simbad-ref new-system-data) (:simbad_ref current-system-data))
                            (not= (:controlling-minor-faction-id new-system-data) (:controlling_minor_faction_id current-system-data))
                            (not= (:reserve-type-id new-system-data) (:reserve_type_id current-system-data))
                            (any-faction-data-changed? current-faction-data new-faction-data)))
                 (change-system! new-system-data new-faction-data :update)
                 (when (nil? current-system-data)
                   (change-system! new-system-data new-faction-data :insert)))
               (errorf "Failed to load row from system data: %s\nError: %s" data (:message err))))

(defsfn system-actor []
  (loop []
    (receive
      [m]

      ;; validate and add the JSONL input for system and its faction
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
