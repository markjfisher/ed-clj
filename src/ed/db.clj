(ns ed.db
  (:require [ed.conf :refer [config]]
            [taoensso.timbre :as timbre :refer [info errorf]]
            [conman.core :as conman]
            [mount.core :refer [defstate]]))

;; delay will stop immediate creation of pool until it is first read
;; however it does mean you have to double-deref it to get at the pool.
;; hence many sections of the code use a let [dba @dba] to remove a layer.

(defstate ^:dynamic *db*
          :start (conman/connect! (:db config))
          :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "ed.sql")

(defn faction-table-exists
  []
  ;; isolate the exception to test if table exists by selecting from it.
  (try (get-faction {:id 0}) true
       (catch Exception _ false)))

(defn drop-db-tables!
  "Drops all database tables"
  []
  (info "drop-db-tables!")
  (drop-faction-table!)
  (drop-government-table!)
  (drop-allegiance-table!)
  (drop-state-table!)
  (drop-reserve-type-table!)
  (drop-primary-economy-table!)
  (drop-power-state-table!)
  (drop-security-table!)
  (drop-faction-state-table!)
  (drop-system-table!)
  (drop-system-faction-table!))

(defn create-db-tables!
  "Initialises an empty database"
  []
  (info "create-db-tables!")
  (create-faction-table!)
  (create-government-table!)
  (create-allegiance-table!)
  (create-state-table!)
  (create-reserve-type-table!)
  (create-primary-economy-table!)
  (create-power-state-table!)
  (create-secuirty-table!)
  (create-faction-state-table!)
  (create-system-table!)
  (create-system-faction-table!)

  ;; constraints
  (create-faction-pk!)
  (create-faction-name-index!)
  (create-government-pk!)
  (create-allegiance-pk!)
  (create-state-pk!)
  (create-reserve-type-pk!)
  (create-primary-economy-pk!)
  (create-power-state-pk!)
  (create-security-pk!)
  (create-faction-state-pk!)
  (create-faction-state-faction-id-index!)
  (create-system-pk!)
  (create-system-name-index!)
  (create-system-faction-pk!)

  (info "create-db-tables: all db tables created"))

(defn init-db!
  "Initialises a database if needed"
  []
  (info "init-db!")
  (when (and (:recreate-db config)
             (faction-table-exists))
    (do (info "Dropping tables")
        (drop-db-tables!)))
  (when-not (faction-table-exists)
    (do
      (info "ED Faction - creating initial table structure")
      (create-db-tables!))))
