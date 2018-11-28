(ns jyvert.core
  (:gen-class)
  (:require [yaml.core :as yaml])
  (:require [clojure.data.json :as json])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as strs])
  (:require [clojure.tools.logging :as log]))

(defn convert-yaml
  "yaml file to json string"
  [file]
  (-> file
      (slurp)
      (yaml/parse-string)
      (json/write-str)))

(defn convert-json
  "json file to yaml string"
  [file]
  (-> file
      (slurp)
      (json/read-str)
      (yaml/generate-string)))

(defn convert
  "Converts yaml to json and json to yaml"
  [file]
  (if (and (.exists (io/file file)) (not (.isDirectory (io/file file))))
    (cond
      (strs/ends-with? file ".json") (spit (strs/replace file #"\.json$" ".yaml") (convert-json file))
      (strs/ends-with? file ".yaml") (spit (strs/replace file #"\.yaml$" ".json") (convert-yaml file))
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
