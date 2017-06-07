(ns ed.errors-test
  (:require [ed.errors :as e]
            [clojure.test :refer :all]))

(deftest attempt-all-tests
  (testing "no errors completes form"
    (is (= {:a 1 :b 2 :c 3}
           (e/attempt-all [empty {}
                           a (merge empty {:a 1})
                           b (merge a {:b 2})
                           c (merge b {:c 3})]
                          c))))

  (testing "no errors completes form named error and else ignored"
    (is (= {:a 1 :b 2 :c 3}
           (e/attempt-all err [empty {}
                               a (merge empty {:a 1})
                               b (merge a {:b 2})
                               c (merge b {:c 3})]
                          c
                          :foo))))

  (testing "error bypasses further bindings uses else with named var set"
    (is (= "Divide by zero"
           (e/attempt-all err [a 0
                               b 1
                               c (/ b a)
                               d (inc c)]
                          d
                          (-> err .getMessage)))))

  (testing "failure within bindings handed to named var"
    (is (= (e/fail "the failure was: i failed")
           (e/attempt-all err [a 0
                               b 1
                               c (e/fail "i failed")
                               d (inc c)]
                          d
                          (e/fail (str "the failure was: " (:message err)))))))

  (testing "failure within bindings surfaces"
    (is (= (e/fail "i failed")
           (e/attempt-all [a 0
                           b 1
                           c (e/fail "i failed")
                           d (inc c)]
                          d))))

  (testing "error bypasses further bindings and passes back the exception"
    (is (= "Divide by zero"
           (.getMessage (e/attempt-all [a 0
                                        b 1
                                        c (/ b a)
                                        d (inc c)]
                                       d))))))
