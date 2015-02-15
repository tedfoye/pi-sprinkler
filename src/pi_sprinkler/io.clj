(ns pi-sprinkler.io
  (:import
   [com.pi4j.io.gpio GpioController]
   [com.pi4j.io.gpio GpioFactory]
   [com.pi4j.io.gpio GpioPinDigitalOutput]
   [com.pi4j.io.gpio PinState]
   [com.pi4j.io.gpio RaspiPin]))

(def gpio-pins (atom {}))

(defn ^:private provision-pin [gpio n s]
  (.. gpio (provisionDigitalOutputPin n s PinState/LOW)))

(defn init []
  (let [gpio (.. GpioFactory (getInstance))]
    ;; clock
    (swap! gpio-pins assoc :clk (provision-pin gpio RaspiPin/GPIO_07 "clk"))
    ;; not output enable
    (swap! gpio-pins assoc :noe (provision-pin gpio RaspiPin/GPIO_00 "noe"))
    ;; data
    (swap! gpio-pins assoc :dat (provision-pin gpio RaspiPin/GPIO_02 "dat"))
    ;; latch
    (swap! gpio-pins assoc :lat (provision-pin gpio RaspiPin/GPIO_03 "lat"))))

(defn out [pin val]
    (condp some [val]
      #{0 :low}  (.. (pin @gpio-pins) (low))
      #{1 :high} (.. (pin @gpio-pins) (high))))

(defn shift-in [zone-states]
  (out :noe :high)
  (out :lat :low)
  
  (doseq [state (reverse zone-states)]
    (out :clk :low)
    (out :dat state)
    (out :clk :high))
  
  (out :lat :high)
  (out :noe :low))
