(ns ed.zmq
  (:gen-class)
  (:require [zeromq.zmq :as zmq]
            [ed.zip :as z]
            [cheshire.core :as jc]
            [clojure.pprint :as pp]))

(defn star-system
  [data]
  (-> data :message :StarSystem))

(defn system-faction
  [data]
  (-> data :message :SystemFaction))

(defn factions
  [data]
  (-> data :message :Factions))

(defmulti process-event
  "Process an EDDN event."
  (fn [data]
    [(:$schemaRef data) (-> data :message :event)]))

(defmethod process-event ["http://schemas.elite-markets.net/eddn/journal/1" "FSDJump"]
  [data]
  (let [name         (star-system data)
        main-faction (system-faction data)
        factions     (factions data)]
    (when main-faction
      (println (format "---------------------------\n%s\nMain Faction: %s\n" name main-faction))
      (doseq [f factions]
        (println (format "    faction: %s\n  influence: %f\n       govt: %s\n      state: %s\n allegiance: %s\n"
                         (:Name f)
                         (:Influence f)
                         (:Government f)
                         (:FactionState f)
                         (:Allegiance f)))
        ))))

(defmethod process-event :default
  [data])

(defn -main []
  (let [context (zmq/zcontext)
        poller (zmq/poller context 2)]
    (with-open [subscriber (doto (zmq/socket context :sub)
                             (zmq/connect "tcp://eddn-relay.elite-markets.net:9500")
                             (zmq/subscribe ""))]
      (zmq/register poller subscriber :pollin)
      (while (not (.. Thread currentThread isInterrupted))
        (zmq/poll poller)
        (when (zmq/check-poller poller 0 :pollin)
          (let [msg-raw (zmq/receive subscriber)
                msg     (slurp (z/inflate msg-raw))
                edn     (jc/parse-string msg true)]
            ;; (pp/pprint edn)
            (process-event edn)))))))
