(ns ed.util
  (:gen-class)
  (:require [ed.errors :refer [attempt-all fail]]
            [clojure.edn :as edn]
            [clojure.pprint :as pprint]
            [clojure.set :refer [difference]]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clj-time.coerce :as coerce]
            [clj-time.format :as format]
            [clj-time.core :as c])
  (:import (java.io File)))

(defn deep-merge
  "Deep merge two maps"
  [& values]
  (if (every? map? values)
    (apply merge-with deep-merge values)
    (last values)))

(defmulti parse-int type)
(defmethod parse-int Integer [n] n)
(defmethod parse-int Long [n] n)
(defmethod parse-int String [s] (Integer/parseInt s))

(defn resource-as-string
  [f]
  (try (-> f
           clojure.java.io/resource
           slurp)
       (catch Exception err
         (fail (format "failed to read %s: %s" f err)))))

(defn load-config
  [& filenames]
  (try (reduce deep-merge (map #(-> %
                                    clojure.java.io/resource
                                    slurp
                                    edn/read-string)
                               filenames))
       (catch Exception err
         (fail (format "failed to load config(s) %s: %s" filenames err)))))

(defn name-to-keyword
  [k]
  "Convert names with underscores to hyphens and return as a keyword"
  (keyword (str/replace k "_" "-")))

(defn distance-between
  "Calculate the distance between two points p1, p2 which are maps with :x :y :z coordinates."
  [p1 p2]
  (Math/pow (+ (Math/pow (- (:x p1) (:x p2)) 2)
               (Math/pow (- (:y p1) (:y p2)) 2)
               (Math/pow (- (:z p1) (:z p2)) 2))
            0.5))

(defn distance-within?
  "Predicate for two points being within a distance of each other"
  [p1 p2 d]
  (> d (distance-between p1 p2)))

(defn rotate [n s]
  (when (and (or (list? s)
                 (vector? s)
                 (seq? s))
             (not (empty? s)))
    (let [shift (mod n (count s))]
      (concat (drop shift s)
              (take shift s)))))


(defn copy-url-to!
  "Copies from a URI to a file."
  [uri file]
  (with-open [in (io/input-stream uri)
              out (io/output-stream file)]
    (io/copy in out)))

(defn create-temp-file-from-url!
  "Copies from url into a temporary file, marking for deletion on exit, returning the file reference"
  [url]
  (let [temp-file (File/createTempFile "ed-data" ".jsonl")]
    (copy-url-to! url temp-file)
    (.deleteOnExit temp-file)
    temp-file))

(defn timestamp-to-int
  "Creates an int value from formatted timestamp relative to epoch in seconds"
  [t]
  (coerce/to-epoch (format/parse (format/formatters :date-time-no-ms) t)))

(defn now-as-int
  "Creates an int value for now relative to epoch in seconds"
  []
  (coerce/to-epoch (c/now)))