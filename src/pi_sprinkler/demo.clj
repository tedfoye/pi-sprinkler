(ns pi-sprinkler.demo
  (:require [pi-sprinkler.io :as io])
  (:gen-class))

(defn -main [& args]
  (println "pi sprinkler demo")
  (io/init)
  (io/shift-in [1 0 0 0 0 0 0 0])
  (Thread/sleep 10000)
  (io/shift-in [0 1 0 0 0 0 0 0])
  (Thread/sleep 10000)
  (io/shift-in [0 0 1 0 0 0 0 0])
  (Thread/sleep 10000)
  (io/shift-in [0 0 0 0 0 0 0 0]))
