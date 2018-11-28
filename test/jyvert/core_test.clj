(ns jyvert.core-test
  (:require [clojure.test :refer :all]
            [jyvert.core :refer :all]
            [clojure.java.io :as io]))

(deftest test-convert
  "Test convert function conversion and condition hanlding"
  (testing "Tests convert function on a directory"
    (convert "test/resources")
    (is (.isDirectory (io/file "test/resources"))))
  (testing "Tests convert function on a missing file"
    (convert "test/resources/NOTFOUND.yaml")
    (is (not (.exists (io/file "test/resources/NOTFOUND.json")))))
  (testing "Tests convert function on a invalid file extension"
    (spit "test/resources/INVALID.yml" "")
    (convert "test/resources/INVALID.yml")
    (is (not (.exists (io/file "test/resources/INVALID.json"))))
    (io/delete-file "test/resources/INVALID.yml")))

(deftest test-convert-yaml
  (testing "Tests convert function for json to yaml equality"
    (is (= (convert-yaml "test/resources/yaml/test.yaml") (slurp "test/resources/json/test.json")))))

  (deftest test-convert-json
    (testing "Tests convert function for yaml to json equality"
      (is (= (convert-json "test/resources/json/test.json") (slurp "test/resources/yaml/test.yaml")))))

(deftest test-main
  "Test that no exceptions are thrown from main"
  (testing "This tests main with no arguments"
    (-main))
  (testing "This tests main with arguments"
    (-main "" "")))
