DOCKER=docker
DOCKER_PREFIX=jam

DOCKER_IMG_NAME=jyvert
DOCKER_BUILDER_IMG_NAME=graal

DOCKER_BUILD_VERSION=latest
DOCKER_BUILDER_VERSION=latest

DOCKER_NAME = $(DOCKER_PREFIX)/$(DOCKER_IMG_NAME):$(DOCKER_BUILD_VERSION)
DOCKER_BUILDER_NAME = $(DOCKER_PREFIX)/$(DOCKER_BUILDER_IMG_NAME):$(DOCKER_BUILDER_VERSION)

DOCKER_BUILD_DIR=.

DOCKER_BUILDER_FILE=Dockerfile-graal
DOCKER_BUILD_FILE=Dockerfile

DOCKER_BUILD_CMD=$(DOCKER) build -t $(DOCKER_NAME) -f $(DOCKER_BUILD_FILE) $(DOCKER_BUILD_DIR)
DOCKER_BUILDER_CMD=$(DOCKER) build -t $(DOCKER_BUILDER_NAME) -f $(DOCKER_BUILDER_FILE) $(DOCKER_BUILD_DIR)
DOCKER_CREATE_CMD=$(DOCKER) create $(DOCKER_NAME)

OUTPUT_FILE=app-standalone
OUTPUT_DIRECTORY=target/
OUTPUT=$(OUTPUT_DIRECTORY)$(OUTPUT_FILE)

SOURCES = $(wildcard src/jyvert/*.clj)
SOURCES += project.clj

.PHONY: clean all

all: $(OUTPUT)

clean:
	rm -f $(OUTPUT)
	rm -f build-graal
	rm -f build-base

build-graal: $(DOCKER_BUILDER_FILE)
	$(DOCKER_BUILDER_CMD)
	touch build-graal # dummy file for timestamp

build-base: build-graal $(DOCKER_BUILD_FILE) $(SOURCES)
	$(DOCKER_BUILD_CMD)
	touch build-base # dummy file for timestamp

$(OUTPUT): build-base
	CID=$$($(DOCKER_CREATE_CMD)) ; \
	$(DOCKER) cp $$CID:/$(OUTPUT_FILE) $(OUTPUT) ; \
	$(DOCKER) rm $$CID
	touch -c $(OUTPUT) # update timestamp from containers internal
