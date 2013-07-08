(ns estate.core
  (:refer-clojure :exclude [read])
  (:import java.util.Properties)
  (:require [clojure.java.io :as io]))

(defn- transform-map
  [m key-fn value-fn]
  (reduce (fn [m [k v]]
            (let [k (key-fn k)]
              (assoc m k (value-fn k v))))
          {}
          m))

(defn read
  [f & options]
  (let [{:keys [key-fn value-fn]} (apply hash-map options)]
    (with-open [r (io/reader f)]
      (let [props (doto (Properties.) (.load r))
            value-fn (or value-fn (fn [k v] v))]
        (transform-map props (or key-fn identity) value-fn)))))

(defn write
  [f m & options]
  (let [{:keys [comments key-fn value-fn]} (apply hash-map options)
        value-fn (or value-fn (fn [k v] (str v)))]
    (with-open [w (io/writer f)]
      (doto (Properties.)
        (.putAll (transform-map m (or key-fn str) value-fn))
        (.store w comments)))))
