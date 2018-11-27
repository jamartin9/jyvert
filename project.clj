(defproject jyvert "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.9.0"]
                 [io.forward/yaml "1.0.9"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.logging "0.4.1"]
                 ]
  :main ^:skip-aot jyvert.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
