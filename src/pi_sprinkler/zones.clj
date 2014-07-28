(ns pi-sprinkler.zones
  (:require [pi-sprinkler.events :as evt]))

(defn ^:private all-off-register []
  (into [] (repeat 8 0)))

(defn ^:private create-register [zone]
  (let [register (into [] (repeat 8 0))]
    (assoc register (- zone 1) 1)))

(defn ^:private build [{:keys [zones start]}]
  (loop [register [] time start zones zones]
    (if (empty? zones)
      (into register [{:z (all-off-register) :t time}])
      (let [[z d] (first zones) d (* 60000 d)]
        (recur (into register [{:z (create-register z) :t time}])
               (+ time d)
               (rest zones))))))

(defn get-zone-events []
  (let [events (evt/current-day-events)]
    (reduce concat (map build events))))
