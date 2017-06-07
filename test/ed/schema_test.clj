(ns ed.schema-test
  (:require [ed.schema :as s]
            [ed.errors :refer [fail attempt-all]]
            [schema.core :as sc]
            [clojure.test :refer :all]))

(deftest validate-test
  (testing "failing schema validate is wrapped with fail"
    (with-redefs [sc/validate (fn [_ _] (throw (Exception. "schema err")))]
      (is (= (fail "schema err") (s/validate :test :case)))))

  (testing "valid schema passes through"
    (with-redefs [sc/validate (fn [_ i] i)]
      (is (= (:pass (s/validate :foo :pass)))))))
