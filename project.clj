(defproject youtube-video-search "0.1.0-SNAPSHOT"
  :description "A small app to search for videos using the YouTube API"
  :url "https://github.com/quanticle/youtube-video-search"
  :license {:name "GPL 3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot youtube-video-search.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
