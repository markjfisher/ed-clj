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

(deftest name-to-keyword-test
  (testing "string names are converted to keywords with hyphens for underscores"
    (is (= :foo-bar (u/name-to-keyword "foo_bar")))
    (is (= :Foo-Bar (u/name-to-keyword "Foo_Bar")))
    (is (= :foo-bar (u/name-to-keyword "foo-bar")))
    (is (= :foobar (u/name-to-keyword "foobar")))))

(deftest distance-tests
  (let [o  {:x 0 :y 0 :z 0}
        p1 {:x 1.0 :y 0 :z 0}
        p2 {:x 0 :y 1.0 :z 0}
        p3 {:x 0 :y 0 :z 1.0}]
    (testing "distance between two points"
      (is (> 0.0001 (Math/abs (- 1.0 (u/distance-between o p1)))))
      (is (> 0.0001 (Math/abs (- 1.0 (u/distance-between p1 o)))))
      (is (> 0.0001 (Math/abs (- 1.0 (u/distance-between o p2)))))
      (is (> 0.0001 (Math/abs (- 1.0 (u/distance-between p2 o)))))
      (is (> 0.0001 (Math/abs (- 1.0 (u/distance-between o p3)))))
      (is (> 0.0001 (Math/abs (- 1.0 (u/distance-between p3 o)))))

      (is (> 0.0001 (Math/abs (- 1.414214 (u/distance-between p1 p2)))))
      (is (> 0.0001 (Math/abs (- 1.414214 (u/distance-between p2 p1)))))
      (is (> 0.0001 (Math/abs (- 1.414214 (u/distance-between p1 p3)))))
      (is (> 0.0001 (Math/abs (- 1.414214 (u/distance-between p3 p1)))))
      (is (> 0.0001 (Math/abs (- 1.414214 (u/distance-between p2 p3)))))
      (is (> 0.0001 (Math/abs (- 1.414214 (u/distance-between p3 p2))))))

    (testing "distance within predicate"
      (is (true? (u/distance-within? o p1 1.1)))
      (is (false? (u/distance-within? o p1 0.9)))
      (is (true? (u/distance-within? p1 p2 1.5)))
      (is (false? (u/distance-within? p2 p1 1.4))))))

(deftest rotate-tests
  (testing "rotating lists"
    (is (= '(1 2 3) (u/rotate 0 '(1 2 3))))
    (is (= '(2 3 1) (u/rotate 1 '(1 2 3))))
    (is (= '(3 1 2) (u/rotate 2 '(1 2 3))))
    (is (= '(1 2 3) (u/rotate 3 '(1 2 3))))
    (is (= '(3 1 2) (u/rotate -1 '(1 2 3))))
    (is (= '(2 3 1) (u/rotate -2 '(1 2 3))))
    (is (= '(1 2 3) (u/rotate -3 '(1 2 3))))
    (is (= '(3 1 2) (u/rotate -4 '(1 2 3)))))

  (testing "rotating vectors"
    (is (= [1 2 3] (u/rotate 0 [1 2 3])))
    (is (= [2 3 1] (u/rotate 1 [1 2 3])))
    (is (= [3 1 2] (u/rotate 2 [1 2 3])))
    (is (= [1 2 3] (u/rotate 3 [1 2 3])))
    (is (= [3 1 2] (u/rotate -1 [1 2 3])))
    (is (= [2 3 1] (u/rotate -2 [1 2 3])))
    (is (= [1 2 3] (u/rotate -3 [1 2 3])))
    (is (= [3 1 2] (u/rotate -4 [1 2 3]))))

  (testing "rotating non sequences or empty collections"
    (is (nil? (u/rotate 0 nil)))
    (is (nil? (u/rotate 0 [])))
    (is (nil? (u/rotate 0 '())))
    (is (nil? (u/rotate 1 nil)))
    (is (nil? (u/rotate 1 '())))
    (is (nil? (u/rotate 1 5)))
    (is (nil? (u/rotate 1 :foo)))))
