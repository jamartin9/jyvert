(ns jyvert.core
  (:gen-class)
  (:require [yaml.core :as yaml])
  (:require [clojure.data.json :as json])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as strs])
  (:require [clojure.tools.logging :as log]))

(defn convert
  "Converts yaml to json and json to yaml"
  [file]
  (if (and (.exists (io/file file)) (not (.isDirectory (io/file file))))
    (cond
      (strs/ends-with? file ".json") (spit (strs/replace file #"\.json$" ".yaml") (yaml/generate-string (json/read-str (slurp file))))
      (strs/ends-with? file ".yaml") (spit (strs/replace file #"\.yaml$" ".json") (json/write-str (yaml/parse-string (slurp file))))
      :else (log/info "Unknown extension for:" file))
    (log/info "File not found or is a directory.")))

(defn -main
  "Converts file path cli arguments to either .json or .yaml files."
  [& args]
  (if-not (empty? args)
    (do
      (dorun (pmap convert args))
      (shutdown-agents))
    (log/info "Supply either .json or .yaml file paths.")))
