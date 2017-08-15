(ns ed.db
  (:gen-class)
  (:require [ed.conf :refer [config]]
            [taoensso.timbre :as timbre :refer [info errorf infof]]
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
  (let [drop-fns []])
  (dorun (for [drop-fn [drop-faction-table!
                        drop-system-table!
                        drop-system-faction-table!]]
           (try (drop-fn)
                (catch Exception e (infof "Could not drop table: %s" (.getMessage e)))))))

(defn create-db-tables!
  "Initialises an empty database"
  []
  (create-faction-table!)
  (create-system-table!)
  (create-system-faction-table!)

  ;; constraints
  (create-faction-pk!)
  (create-faction-name-index!)
  (create-system-pk!)
  (create-system-name-index!)
  (create-system-faction-pk!)
  (create-system-faction-system-index!)

  (info "create-db-tables: all db tables created"))

(defn init-db!
  "Initialises a database if needed"
  []
  (info "init-db!")
  (when (get-in config [:app :new-db] true)
    (do (info "Dropping tables")
        (drop-db-tables!)))
  (when-not (faction-table-exists)
    (do
      (info "ED Faction - creating initial table structure")
      (create-db-tables!))))
