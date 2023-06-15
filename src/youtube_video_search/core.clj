(ns youtube-video-search.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as http-client]
            [clj-http.util :as http-util]
            [clojure.data.json :as json]
            [clojure.instant :as time])
  (:import [java.time Duration])
  (:gen-class))

(def client-key-file-name "client-key")
(def search-url "https://www.googleapis.com/youtube/v3/search")
(def video-info-url "https://www.googleapis.com/youtube/v3/videos")
(defrecord video-info [upload-date video-title video-id duration])

(defn load-client-key
  "Loads the client key from the given file. The file is assumed to have just
   the API key, with a possible trailing newline."
  [file-name]
  (str/trim (slurp (io/resource file-name))))

(defn format-search-query
  "Turns a search query with OR and NOT into a search query with | and -, 
   suitable for the API"
  [search-query]
  (str/replace (str/replace search-query #" OR " "|") #"NOT " "-"))

(defn send-search-query
  "Sends a search query to the YouTube API and returns the results as a map"
  [api-key search-query]
  (let [search-response (http-client/get search-url
                                         {:accept :json
                                          :query-params {:key api-key
                                                         :part "snippet"
                                                         :maxResults 50
                                                         :type "video"
                                                         :q search-query}})]
    (json/read-str (:body search-response) :key-fn keyword)))

(defn get-video-ids-from-search-results 
  "Extracts the video IDs from search results returned by SEND-SEARCH-QUERY"
  [search-results]
  (map #(get-in % [:id :videoId]) (:items search-results)))

(defn get-video-data-for-video-ids
  "Gets the video data for a list of up to 50 video IDs"
  [api-key video-ids]
  (let [video-id-str (str/join "," video-ids)
        video-info-response (http-client/get video-info-url
                                             {:accept :json
                                              :query-params {:key api-key
                                                             :part "id,snippet,contentDetails"
                                                             :id video-id-str}})]
    (map #(->video-info (time/read-instant-date (get-in % [:snippet :publishedAt]))
                        (get-in % [:snippet :title])
                        (:id %)
                        (Duration/parse (get-in % [:contentDetails :duration])))
         (-> video-info-response
             :body
             (json/read-str :key-fn keyword)
             :items))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
