(ns ed.util
  (:require [ed.errors :refer [attempt-all fail]]
            [clojure.edn :as edn]
            [clojure.pprint :as pprint]
            [clojure.set :refer [difference]]))

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

