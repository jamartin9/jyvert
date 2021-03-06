DOCKER ?= docker
DOCKER_PREFIX ?= jam

DOCKER_BUILD_IMG_NAME ?= jyvert
DOCKER_BUILDER_IMG_NAME ?= graal

DOCKER_BUILD_VERSION ?= latest
DOCKER_BUILDER_VERSION ?= latest

DOCKER_BUILD_FILE ?= Dockerfile
DOCKER_BUILDER_FILE ?= Dockerfile-graal

DOCKER_BUILD_DIR ?= .

SOURCES ?= $(wildcard src/jyvert/*.clj) project.clj

OUTPUT_DIR ?= target/

OUTPUT_FILE := app-standalone
OUTPUT := $(OUTPUT_DIR)$(OUTPUT_FILE)

DOCKER_BUILD_NAME := $(DOCKER_PREFIX)/$(DOCKER_BUILD_IMG_NAME):$(DOCKER_BUILD_VERSION)
DOCKER_BUILDER_NAME := $(DOCKER_PREFIX)/$(DOCKER_BUILDER_IMG_NAME):$(DOCKER_BUILDER_VERSION)

DOCKER_BUILD_CMD := $(DOCKER) build -t $(DOCKER_BUILD_NAME) -f $(DOCKER_BUILD_FILE) $(DOCKER_BUILD_DIR)
DOCKER_BUILDER_CMD := $(DOCKER) build -t $(DOCKER_BUILDER_NAME) -f $(DOCKER_BUILDER_FILE) $(DOCKER_BUILD_DIR)

DOCKER_CREATE_CMD := $(DOCKER) create $(DOCKER_BUILD_NAME)

.PHONY: clean all

all: $(OUTPUT)

clean:
	rm -f $(OUTPUT)
	rm -f build-graal
	rm -f build-base

build-graal: $(DOCKER_BUILDER_FILE)
	$(DOCKER_BUILDER_CMD)
	touch build-graal

build-base: build-graal $(DOCKER_BUILD_FILE) $(SOURCES)
	$(DOCKER_BUILD_CMD)
	touch build-base

$(OUTPUT): build-base
	mkdir -p $(OUTPUT_DIR)
	CID=$$($(DOCKER_CREATE_CMD)) ; \
	$(DOCKER) cp $$CID:/$(OUTPUT_FILE) $(OUTPUT) ; \
	$(DOCKER) rm $$CID
	touch -c $(OUTPUT)
