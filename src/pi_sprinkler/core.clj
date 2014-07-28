(ns pi-sprinkler.core
  (:require [clojure.core.async :refer [<! >! <!! alt! chan go go-loop timeout]]
            [pi-sprinkler.zones :as zones])
  (:import [com.pi4j.io.gpio GpioController]
           [com.pi4j.io.gpio GpioFactory]
           [com.pi4j.io.gpio GpioPinDigitalOutput]
           [com.pi4j.io.gpio PinState]
           [com.pi4j.io.gpio RaspiPin])
  (:gen-class))

(def gpio (.. GpioFactory (getInstance)))
(def clk (.. gpio (provisionDigitalOutputPin RaspiPin/GPIO_07 "clk" PinState/LOW)))
(def noe (.. gpio (provisionDigitalOutputPin RaspiPin/GPIO_00 "noe" PinState/LOW)))
(def dat (.. gpio (provisionDigitalOutputPin RaspiPin/GPIO_02 "dat" PinState/LOW)))
(def lat (.. gpio (provisionDigitalOutputPin RaspiPin/GPIO_03 "lat" PinState/LOW)))

(defn ^:private disable-output [] (.. noe (high)))
(defn ^:private enable-output [] (.. noe (low)))

(defn ^:private shift-in [zone-states]
  (disable-output)
  (.. clk (low))
  (.. lat (low))
  (doseq [state (reverse zone-states)]
    (.. clk (low))
    (if (= 0 state) (.. dat (low)) (.. dat (high)))
    (.. clk (high)))
  (.. lat (high))
  (enable-output))

(defn ^:private init []
  (disable-output)
  (shift-in [0 0 0 0 0 0 0 0])
  (enable-output))

(defn ^:private active-event [events]
  (let [time (* (long (/ (System/currentTimeMillis) 1000)) 1000)]
    (some (fn [event] (if (= time (:t event)) event)) events)))

(defn ^:private create-events-chan []
  (let [c (chan)]
    (go-loop []
      (<! (timeout 30000))
      (>! c (zones/get-zone-events))
      (recur))
    c))

(defn run [event-chan]
  (go-loop [t (timeout 1000) events (zones/get-events)]
    (let [events (alt! [event-chan t] ([v c] (if (= c event-chan) v events)))]
      (if-let [event (active-event events)] (shift-in (:z event)))
      (recur (timeout 1000) events))))

(defn -main [& args]
  (println "pi sprinkler running")
  (init)
  (<!! (run (create-events-chan))))
