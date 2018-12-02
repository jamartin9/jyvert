(ns jyvert.core
  (:gen-class)
  (:require [yaml.core :as yaml])
  (:require [clojure.data.json :as json])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as strs])
  (:require [clojure.tools.logging :as log])
  (:require [clojure.spec.alpha :as s])
  (:require [clojure.spec.gen.alpha :as gen]))

(s/def ::is-nil nil?)
(s/def ::is-string string?)
(s/def ::is-json (s/and ::is-string #(strs/ends-with? % ".json")))
(s/def ::is-yaml (s/and ::is-string #(strs/ends-with? % ".yaml")))
(s/def ::is-valid-file-path (s/and ::is-string
                                   #(.exists (io/file %))
                                   #(not (.isDirectory (io/file %)))))

(s/fdef convert-yaml
  :args (s/cat :file (s/and ::is-valid-file-path
                            ::is-yaml))
  :ret ::is-string)
(defn convert-yaml
  "yaml file to json string"
  [file]
  {:pre [(s/valid? (s/and ::is-yaml ::is-valid-file-path) file)]
   :post [(s/valid? ::is-string %)]}
  (-> file
      (slurp)
      (yaml/parse-string)
      (json/write-str)))

(s/fdef convert-json
  :args (s/cat :file (s/and ::is-valid-file-path
                            ::is-json))
  :ret ::is-string)
(defn convert-json
  "json file to yaml string"
  [file]
  {:pre [(s/valid? (s/and ::is-json ::is-valid-file-path) file)]
   :post [(s/valid? ::is-string %)]}
  (-> file
      (slurp)
      (json/read-str)
      (yaml/generate-string)))

(s/fdef write-yaml
  :args (s/cat :file (s/and ::is-json ::is-valid-file-path))
  :ret ::is-nil)
(defn write-yaml
  "Writes yaml from json conversion to file."
  [file]
  {:pre [(s/valid? (s/and ::is-json ::is-valid-file-path) file)]
   :post [(.exists (io/file (strs/replace file #"\.json$" ".yaml")))]}
  (spit (strs/replace file #"\.json$" ".yaml") (convert-json file)))

(s/fdef write-json
  :args (s/cat :file (s/and ::is-yaml ::is-valid-file-path))
  :ret ::is-nil)
(defn write-json
  "Writes json from yaml conversion to file"
  [file]
  {:pre [(s/valid? (s/and ::is-yaml ::is-valid-file-path) file)]
   :post [(.exists (io/file (strs/replace file #"\.yaml$" ".json")))]}
  (spit (strs/replace file #"\.yaml$" ".json") (convert-yaml file)))

(s/fdef convert-file
  :args (s/cat :file-path ::is-string)
  :ret ::is-nil)
(defn convert-file
  "Writes to file based on string ending"
  [file]
  (cond
    (strs/ends-with? file ".json") (write-yaml file)
    (strs/ends-with? file ".yaml") (write-json file)
    :else (log/info "Unknown extension for:" file)))

(s/fdef convert
  :args (s/cat :file-path ::is-string)
  :ret ::is-nil)
(defn convert
  "Converts yaml to json and json to yaml"
  [file]
  (if (and (.exists (io/file file))
           (not (.isDirectory (io/file file))))
    (convert-file file)
    (log/info "File not found or is a directory:" file)))

(s/fdef -main
  :args (s/cat :args (s/* (s/cat :arg ::is-string)))
  :ret ::is-nil)
(defn -main
  "Converts file path cli arguments to either .json or .yaml files."
  [& args]
  (if-not (empty? args)
    (do
      (dorun (pmap convert args))
      (shutdown-agents))
    (log/info "Supply either .json or .yaml file paths.")))
