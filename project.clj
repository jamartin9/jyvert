(defproject jyvert "0.1.0-SNAPSHOT"
  :description "Simple JSON/YAML cli converter"
  :url "http://github.com/jamartin9/jyvert"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.10.0-RC2" :scope "provided"]
                 [io.forward/yaml "1.0.9" :exclusions [org.yaml/snakeyaml org.clojure/clojure]]
                 ; snakeyaml fixed graal issues in version > 1.22
                 [org.yaml/snakeyaml "1.23"]
                 [org.clojure/data.json "0.2.6" :exclusions[org.clojure/clojure]]
                 [org.clojure/tools.logging "0.4.1" :exclusions [org.clojure/clojure]]
                 ]
  :main ^:skip-aot jyvert.core
  ; reduces binary size
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
