(ns ed.loaders
  (:gen-class)
  (:require [ed.conf :refer [config]]
            [ed.util :as u]
            [ed.db :as d]
            [ed.errors :refer [attempt-all fail]]
            [taoensso.timbre :as timbre :refer [info infof errorf]]
            [co.paralleluniverse.pulsar.actors :refer [! spawn]]
            [co.paralleluniverse.pulsar.core :refer [defsfn join]]
            [clojure.java.io :as io]))

(def lookup-data (atom {:allegiance      {}
                        :government      {}
                        :power-state     {}
                        :primary-economy {}
                        :security        {}
                        :state           {}}))

;; when we start up, get and read latest systems file.
;; check for allegiance/state/etc (x6) values, load them into memory.
;; Do we need to ever write them to d/b?
;; They are only needed to lookup string values back to ids to store against system/faction.
;; We can scan the file twice. Once to load the sub-data, 2nd to load the system and faction information.

(defn show-db-data-error
  [type ld dbd]
  (errorf "error in d/b lookup data: type: %s, lookup data: %s, db data: %s" type ld dbd))

(defn update-db-lookup-data!
  "For the given lookup type (e.g. :allegiance) and a db find function (e.g. get-allegiance-by-name)
  find any values in the current lookup-data that are not stored in the database and write them out using insert-fn.
  If the string key is matched and the id is different, raise an error, as the code has changed."
  [type find-fn insert-fn]
  (dorun (for [[k v] (get-in @lookup-data [type])]
           (let [db-record (find-fn {:id v})
                 id (:id db-record)
                 name (:name db-record)]
             (if (and (some? name)
                      (not= k name))
               (show-db-data-error type {k v} db-record)
               (when (nil? id)
                 (insert-fn {:id v :name k})))))))

;; can't see any reason to store this data yet, other than to ensure a datafile doesn't corrupt the values, or
;; miss them if loading a dataset that doesn't have any particular code in it.
(defn write-lookup-data!
  []
  (update-db-lookup-data! :allegiance d/get-allegiance d/insert-allegiance)
  (update-db-lookup-data! :government d/get-government d/insert-government)
  (update-db-lookup-data! :power-state d/get-power-state d/insert-power-state)
  (update-db-lookup-data! :primary-economy d/get-primary-economy d/insert-primary-economy)
  (update-db-lookup-data! :security d/get-security d/insert-security)
  (update-db-lookup-data! :state d/get-state d/insert-state))

(defn show-lookup-data-error
  [type k-str v-id old-value]
  (errorf "error in lookup data: type: %s, string key: %s, new value: %d, old value: %d" type k-str v-id old-value))

(defn update-lookup-data!
  "updates the lookup-data atom for the given type with key k-str and value v-id, erroring if a different value already exists.
  Note that lookups go from string to integer, as the journal entries are by string name, not IDs."
  [type k-str v-id]
  (let [old-value (get-in @lookup-data [type k-str])]
    (if (and (some? old-value)
             (not= v-id old-value))
      (show-lookup-data-error type k-str v-id old-value)
      (when (and (some? k-str)
                 (nil? old-value))
        (swap! lookup-data (fn [v] (assoc-in v [type k-str] v-id)))))))

#_(defn load-lookup-data!
  "Loads into memory the allegiance/state/security/government/primary_economy/power-state data from url."
  [url]
  (with-open [rdr (clojure.java.io/reader url)]
    (dorun (for [jsonl (line-seq rdr)]
             (attempt-all err [system-data (validate-system jsonl)]
                          (do (update-lookup-data! :allegiance (:allegiance system-data) (:allegiance-id system-data))
                              (update-lookup-data! :government (:government system-data) (:government-id system-data))
                              (update-lookup-data! :power-state (:power-state system-data) (:power-state-id system-data))
                              (update-lookup-data! :primary-economy (:primary-economy system-data) (:primary-economy-id system-data))
                              (update-lookup-data! :security (:security system-data) (:security-id system-data))
                              (update-lookup-data! :state (:state system-data) (:state-id system-data)))
                          (errorf "Failed to load row from system data: %s\nError: %s" jsonl (:message err)))))))

(defsfn load-data-for!
  "Send the data line by line to the appropriate handler with the given message type"
  [url handler message-key]
  (with-open [rdr (clojure.java.io/reader url)]
    (dorun (for [jsonl (line-seq rdr)]
             (! handler [message-key jsonl])))))

(defn load-data!
  "Load faction and system data from source files"
  []
  (let [sys-data-file (u/create-temp-file-from-url! (get-in config [:eddb :systems-populated]))
        fac-data-file (u/create-temp-file-from-url! (get-in config [:eddb :factions]))
        floader (spawn load-data-for! (.getAbsolutePath fac-data-file) :faction-handler :add-json-faction)
        sloader (spawn load-data-for! (.getAbsolutePath sys-data-file) :system-handler :add-json-system)]
    #_(info "loading lookup data")
    #_(load-lookup-data! (.getAbsolutePath sys-data-file))

    (info "loading faction/system data")
    (join floader)
    (join sloader)
    (info "completed sending faction/system data to handlers")
    (.delete sys-data-file)
    (.delete fac-data-file)))
