(ns pi-sprinkler.events
  (:require
   [clojure.java.io :as io]
   [clj-time.core :as t]
   [clj-time.coerce :as tc]
   [clj-time.local :as l])
  (:import
   [com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp]
   [com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver]
   [com.google.api.client.googleapis.auth.oauth2 GoogleAuthorizationCodeFlow$Builder]
   [com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets]
   [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
   [com.google.api.client.json.jackson2 JacksonFactory]
   [com.google.api.client.util DateTime]
   [com.google.api.client.util.store FileDataStoreFactory]
   [com.google.api.services.calendar Calendar$Builder]
   [com.google.api.services.calendar CalendarScopes]
   [com.google.api.services.calendar.model Event]
   [com.google.api.services.calendar.model Events]
   [java.util HashSet]))

(def application-name "Sprinkler")
(def json-factory (.. JacksonFactory (getDefaultInstance)))
(def http-transport (.. GoogleNetHttpTransport (newTrustedTransport)))
(def data-store-dir (java.io.File. (.. System (getProperty "user.home")) ".store/calendar_credential"))
(def data-store-factory (FileDataStoreFactory. data-store-dir))

(defn ^:private load-client-secrets []
  (let [resource (-> "client_secrets.json" (io/resource) (io/reader))]
    (GoogleClientSecrets/load json-factory resource)))

(defn ^:private authorize [secrets]
  (let [scopes (HashSet. #{CalendarScopes/CALENDAR CalendarScopes/CALENDAR_READONLY})
        builder (GoogleAuthorizationCodeFlow$Builder. http-transport json-factory secrets scopes)
        flow (.. builder (setDataStoreFactory data-store-factory) (build))
        credential (..  (AuthorizationCodeInstalledApp. flow (LocalServerReceiver.)) (authorize "user"))]
    credential))

(defn ^:private create-calendar [credential]
  (.. (Calendar$Builder. http-transport json-factory credential)
      (setApplicationName application-name)
      (build)))

(defn ^:private min-max-times
  "midnight and 23:59:59.999 for the current day"
  []
  (let [min (tc/to-date-time (t/today)) 
        max (t/minus (t/plus min (t/days 1)) (t/millis 1))
        min (t/from-time-zone min (t/default-time-zone))
        max (t/from-time-zone max (t/default-time-zone))]
    {:min (DateTime. (tc/to-long min)) :max (DateTime. (tc/to-long max))}))

(defn ^:private parse-zones [s]
  (let [re #"(zone).+?([\d]+).+?(\d+)"]
    (for [[_ _ zone dur] (re-seq re s)] [(Integer. zone) (Integer. dur)])))

(defn ^:private list-events [client minmax]
  (let [list (.. client (events)
                 (list "id@group.calendar.google.com"))
        items (.. list
                  (setTimeMin (:min minmax))
                  (setTimeMax (:max minmax))
                  (execute)
                  (getItems))]
    (map (fn [item]
           (let [start (.. item (getStart) (getDateTime) (getValue))
                 description (.. item (getDescription))
                 zones (parse-zones description)]
             {:start start :zones zones}))
         items)))

(defn current-day-events []
  (if-let [credential (authorize (load-client-secrets))] 
    (list-events (create-calendar credential) (min-max-times))
    {}))
