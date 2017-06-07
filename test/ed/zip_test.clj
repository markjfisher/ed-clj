(ns ed.zip-test
  (:require [ed.zip :as z]
            [clojure.test :refer :all]))

(deftest inflate-test
  (is (= "foo" (-> "foo"
                   z/utf8-bytes
                   z/deflate
                   z/inflate
                   slurp))))

(deftest utf8-string-test
  (is (= "foo" (-> "foo"
                   z/utf8-bytes
                   z/utf8-string))))
