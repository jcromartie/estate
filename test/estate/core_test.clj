(ns estate.core-test
  (:use clojure.test)
  (:require [clojure.java.io :as io]
            [estate.core :as core]))

(def test-props {:key "val" :bool true :num 99})

(defn read-props
  [f]
  (core/read f
             :key-fn keyword
             :value-fn (fn [k v]
                         (cond
                          (= k :bool) (Boolean/parseBoolean v)
                          (= k :num) (Long/parseLong v)
                          :else v))))

(deftest read-test
  (is (= test-props (read-props (io/resource "estate/test.properties")))))

(deftest write-test
  (let [s (with-open [w (java.io.StringWriter.)]
            (core/write w test-props :key-fn name)
            (str w))]
    (with-open [r (java.io.StringReader. s)]
      (is (= test-props (read-props r))))))
