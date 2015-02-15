(ns pi-sprinkler.google
  (:import
   [com.google.api.client.googleapis.auth.oauth2 GoogleCredential$Builder]
   [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
   ;[com.google.api.client.http HttpTransport]
   [com.google.api.client.json.jackson2 JacksonFactory]
   [com.google.api.client.util DateTime]
   [com.google.api.services.calendar Calendar$Builder]
   [com.google.api.services.calendar CalendarScopes]
   [java.io File]
   [java.util HashSet]))

(def app-name       "Sprinkler")
(def calendar-id    "49ova9449hf6jsfp904kuqboec@group.calendar.google.com")
(def account-id     "37679462864-hha4at3mtqqjpjvjk2nmnvmgumf838i4@developer.gserviceaccount.com")
(def json-factory   (.. JacksonFactory (getDefaultInstance)))
(def http-transport (.. GoogleNetHttpTransport (newTrustedTransport)))
(def scopes         (HashSet. #{CalendarScopes/CALENDAR CalendarScopes/CALENDAR_READONLY}))
(def one-hour       (* 1000 60 60))
                                
(defn ^:private private-key []
  (File. "src/Sprinkler-c7e9b389d630.p12"))

(defn build-credential []
  (.. (GoogleCredential$Builder.)    
    (setTransport http-transport)
    (setJsonFactory json-factory)
    (setServiceAccountId account-id)
    (setServiceAccountScopes scopes)
    (setServiceAccountPrivateKeyFromP12File (private-key))
    (build)))

(defn ^:private create-calendar [credential]
  (.. (Calendar$Builder. http-transport json-factory credential)
    (setApplicationName "Sprinkler")
    (build)))

(defn ^:private parse-zones [s]
  (let [re #"(zone).+?([\d]+).+?(\d+)"]
    (for [[_ _ zone dur] (re-seq re s)]
      [(Integer. zone) (Integer. dur)])))

(defn query-calendar []
  (let [service (create-calendar (build-credential))
        events (.. service (events) (list calendar-id))
        min (DateTime. (- (System/currentTimeMillis) one-hour))
        max (DateTime. (+ (System/currentTimeMillis) one-hour))
        query (.. events (setTimeMin min) (setTimeMax max))]
    (.. query (execute) (getItems))))

(defn current-events []
  (for [event (query-calendar)]
    (let [start (.. event (getStart) (getDateTime) (getValue))
          zones (parse-zones (.. event (getDescription)))]
      {:start start :zones zones})))

