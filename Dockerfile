FROM clojure as builder
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY src /usr/src/app/src
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

FROM jam/graal as native
WORKDIR /tmp
COPY --from=builder /usr/src/app/app-standalone.jar ./
# graal reflection json config for snakeyaml
COPY graal.json .
RUN graalvm-ce-${GRAALVM_V}/bin/native-image \
  --no-server \
  --static \
  -H:ReflectionConfigurationFiles=/tmp/graal.json \
#  -H:+ReportUnsupportedElementsAtRuntime \
  -jar /tmp/app-standalone.jar

FROM scratch
COPY --from=native /tmp/app-standalone .
ENTRYPOINT ["/app-standalone"]
