(ns ed.util-test
  (:require [clojure.test :refer :all]
            [ed.errors :refer [fail]]
            [ed.util :as u]))

(deftest deep-merge-tests
  (testing "merging maps works as expected"
    (is (= {:a {:b 1 :c 2} }
           (u/deep-merge {:a {:b 1}} {:a {:c 2}})))
    (is (nil? (u/deep-merge {:a {:b 1}} nil)))
    (is (= {:a {:b 1}}
           (u/deep-merge {:a {:b 1}} {})))
    (is (= {:a {:b 1}}
           (u/deep-merge {} {:a {:b 1}})))
    (is (= {:a {:b 1}}
           (u/deep-merge nil {:a {:b 1}})))))

(deftest parse-int-tests
  (testing "parsing string returns int value"
    (is (= 123 (u/parse-int "123"))))

  (testing "parsing an integer returns itself"
    (is (= 123 (u/parse-int (int 123))))))

(deftest resource-as-string-test
  (testing "reading resource on path"
    (is (= "this is resource 1\n" (u/resource-as-string "res1.txt")))
    (is (= "this is resource 2\n" (u/resource-as-string "res2.txt")))
    (is (.startsWith (:message (u/resource-as-string "not-found.txt")) "failed to read not-found.txt:"))))

(deftest load-config-test
  (testing "loading an edn file sets map values"
    (let [d (u/load-config "test-config.edn")]
      (is (= :rocks (:clojure d)))
      (is (= :bar   (:foo d)))
      (is (= 1      (:a d)))
      (is (= [2 3]  (:b d)))))

  (testing "missing resource returns appropriate fail"
    (is (.startsWith (:message (u/load-config "missing-file.edn")) "failed to load config(s) (\"missing-file.edn\"):"))))

