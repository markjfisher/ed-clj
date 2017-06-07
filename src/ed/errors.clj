(ns ed.errors
  (:require [clojure.algo.monads :refer [maybe-m domonad m-result m-bind defmonad]]))

;; see https://brehaut.net/blog/2011/error_monads
;; This class allows us to write much cleaner error handling that threads through a series of code.
;; I've altered the original code to call a function on the error case with the failure object

;; So instead of threading the error and nested let/ifs:
;; (fn [ctx]
;;  (let [{:keys [item error]} (ru/parse-json ctx)]
;;    (if error
;;      error
;;      (let [{:keys [item error]} (s/validate-queue-item item @d/data)]
;;        (if error
;;          error
;;          [false {::data item}])))))

;; we instead write:
;; (fn [ctx]
;;   (attempt-all [json (ru/parse-json ctx)
;;                 item (s/validate-queue-item json @d/data)]
;;     [false {::data item}]
;;     (fn [e]
;;       (handle-error e))))

;; and each function in the attempt-all just throws an exception, or calls (fail "my message")
;; if it is in error

(defrecord Failure [message])
(defn fail [message] (Failure. message))

(defprotocol ComputationFailed
  "A protocol that determines if a computation has resulted in a failure.
   This allows the definition of what constitutes a failure to be extended
   to new types by the consumer."
  (has-failed? [self]))

(extend-protocol ComputationFailed
  nil
  (has-failed? [self] false)

  Object
  (has-failed? [self] false)

  Failure
  (has-failed? [self] true)

  Exception
  (has-failed? [self] true))

(defmonad error-m
  [m-result identity
   m-bind   (fn [m f] (if (has-failed? m)
                        m
                        (try
                          (f m)
                          (catch Exception e
                            e))))])

(defmacro attempt-all
  ([bindings return] `(domonad error-m ~bindings ~return))
  ([name bindings return] `(domonad error-m ~bindings ~return))
  ([name bindings return else]
   `(let [result# (attempt-all ~name ~bindings ~return)]
      (if (has-failed? result#)
        (let [~name result#]
          ~else)
        result#))))
