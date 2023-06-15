(ns youtube-video-search.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [mockery.core :refer :all]
            [youtube-video-search.core :refer :all]))

(deftest test-format-search-query
  (testing "Test OR replacement"
    (is (= "cat|dog" (format-search-query "cat OR dog"))))
  (testing "Test NOT replacement"
    (is (= "cat -dog" (format-search-query "cat NOT dog"))))
  (testing "Test combination replacement"
    (is (= "cat|bunny -dog" (format-search-query "cat OR bunny NOT dog")))))

(deftest test-send-search-query
  (testing "Search query should call the correct URL with the correct params"
    (with-mocks [mock-http-client {:target :clj-http.client/get
                                   :return {:status 200
                                            :body "{\"response\": \"successful request\"}"}}]
      (is (= {:response "successful request"}
           (send-search-query "mock-api-key" "test search request")))
      (is (= [search-url {:accept :json
                          :query-params {:key "mock-api-key"
                                         :part "snippet"
                                         :maxResults 50
                                         :type "video"
                                         :q "test search request"}}]
             (:call-args @mock-http-client))))))

(deftest test-extract-video-ids-from-search-results
  (testing "Should get video ids from search results"
    (let [search-results (json/read-str (slurp (io/resource "test_search_results.json")) :key-fn keyword)
          video-ids (get-video-ids-from-search-results search-results)]
      (is (= 50 (count video-ids)))
      (is (= "XM1NTfC-f08" (first video-ids)))
      (is (= "maT1jyPY0z4" (last video-ids))))))
