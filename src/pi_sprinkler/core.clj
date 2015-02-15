(ns pi-sprinkler.core
  (:require [clojure.core.async :refer [<! >! <!! alt! chan go-loop timeout]]
            [pi-sprinkler.events :as events]
            [pi-sprinkler.io :as io])
  (:gen-class))

(defn ^:private active-event [events]
  (let [time (* (long (/ (System/currentTimeMillis) 1000)) 1000)]
    (some (fn [event] (if (= time (:t event)) event)) events)))

(defn ^:private create-events-chan []
  (let [c (chan)]
    (go-loop []
      (<! (timeout 30000))
      (>! c (events/get-zone-events))
      (recur))
    c))

(defn run [event-chan]
  (go-loop [t (timeout 1000)
            events (events/get-zone-events)]
    (let [events (alt! [event-chan t] ([v c] (if (= c event-chan) v events)))]
      (if-let [event (active-event events)]
        (io/shift-in (:z event)))
      (recur (timeout 1000) events))))

(defn -main [& args]
  (println "pi sprinkler running")
  (io/init)
  (io/shift-in [0 0 0 0 0 0 0 0])
  (<!! (run (create-events-chan)))
  (comment
    (io/shift-in [1 0 0 0 0 0 0 0])
    (Thread/sleep 2000)
    (io/shift-in [1 1 0 0 0 0 0 0])
    (Thread/sleep 2000)
    (io/shift-in [0 1 0 0 0 0 0 0])
    (Thread/sleep 2000)
    (io/shift-in [1 0 0 0 0 0 0 0])
    (Thread/sleep 2000)
    (io/shift-in [0 0 0 0 0 0 0 0])))
