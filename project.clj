(defproject pi-sprinkler "0.1.0-SNAPSHOT"
  :description "A sprinkler system application that runs on a Raspberry Pi that talks to an OpenSprinkler Pi board"
  :url "https://github.com/tedfoye/pi-sprinkler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.pi4j/pi4j-core "1.0-SNAPSHOT"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.google.api-client/google-api-client "1.19.1"]
                 [com.google.apis/google-api-services-calendar "v3-rev112-1.19.1"]]
  :repositories [["sonatype" "https://oss.sonatype.org/content/groups/public"]]
  :plugins [[cider/cider-nrepl "0.8.2"]]
  :main pi-sprinkler.core
  :aot [pi-sprinkler.core pi-sprinkler.demo]
  :target-path "target/%s"
  :uberjar-exclusions ["cider"])
