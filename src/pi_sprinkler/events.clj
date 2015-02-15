(ns pi-sprinkler.events
  (:require [pi-sprinkler.google :as goog]))

(def all-zones-off (into [] (repeat 8 0)))

(defn ^:private create-register [zone]
  (assoc all-zones-off (dec zone) 1))

(defn ^:private build [{:keys [zones start]}]
  (loop [register [] time start zones zones]
    (if (empty? zones)
      (into register [{:z all-zones-off :t time}])
      (let [[z d] (first zones) d (* 60000 d)]
        (recur (into register [{:z (create-register z) :t time}])
               (+ time d)
               (rest zones))))))

(defn get-zone-events []
  (let [events (goog/current-events)]
    (reduce concat (map build events))))
