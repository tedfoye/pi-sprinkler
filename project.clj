(defproject pi-sprinkler "0.1.0-SNAPSHOT"
  :description "A sprinkler system application that runs on a Raspberry Pi that talks to an OpenSprinkler Pi board"
  :url "https://github.com/tedfoye/pi-sprinkler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.pi4j/pi4j-core "0.0.5"]
                 [clj-time "0.7.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [com.google.apis/google-api-services-calendar "v3-rev81-1.18.0-rc"]
                 [com.google.http-client/google-http-client-jackson2 "1.18.0-rc"]
                 [com.google.oauth-client/google-oauth-client-jetty "1.18.0-rc"]]
  :plugins [[cider/cider-nrepl "0.7.0-SNAPSHOT"]]
  :main pi-sprinkler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
