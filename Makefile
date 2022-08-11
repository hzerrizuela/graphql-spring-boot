
#
# Globals
#
ECR_REPOSITORY:=294290347293.dkr.ecr.us-east-1.amazonaws.com
CONTAINER_NAME:=java-spring-template
TAG:=$(shell git log -1 --pretty=format:"%H")
CURRENT_DIR=$(shell pwd)
PLATFORM_ARCHITECTURE:=$(shell uname -m)

.PHONY: clean
clean:
	./gradlew clean

##
## Local Gradle
##
.PHONY: build
build: fixlint
  # Build local and run unit tests
	./gradlew build

.PHONY: build-live
build-live:
  # Rebuild class files if change detected
	./gradlew -t classes --console=plain
.PHONY: verify-coverage
verify-coverage: build
	./gradlew jacocoTestCoverageVerification

.PHONY: run
run: run-dependencies
	# run the app
	./gradlew bootRun --args='--server.port=8080 --spring.profiles.active=dev'

.PHONY: run-live
run-live: run-dependencies
  # Run locally with live reloading enabled
	make -j 2 build-live run

.PHONY: fixlint
fixlint:
	#fix linting problems
	./gradlew spotlessApply

##
## Local Docker
##
.PHONY: run-dependencies
run-dependencies:
	# start dependent services in background
	docker-compose up -d --no-recreate postgres

.PHONY: build-docker
build-docker:
	./gradlew jibBuildTar
	docker load --input build/jib-image.tar

.PHONY: run-docker
run-docker: build-docker run-dependencies
	# start app in foreground
	docker-compose up java-spring-template

.PHONY: down
down:
	docker-compose down

##
## Jenkins Docker
##
.PHONY: jenkins-build
jenkins-build:
	docker run --rm -v $(CURRENT_DIR):/app -w=/app gradle:7.3.3-jdk17-alpine /bin/sh -c "apk add build-base; gradle --stacktrace --no-daemon clean build jacocoTestCoverageVerification"

.PHONY: jenkins-package
jenkins-package:
	docker run --rm -v $(CURRENT_DIR):/app -w=/app gradle:7.3.3-jdk17-alpine /bin/sh -c "apk add build-base; gradle --stacktrace --no-daemon clean build jacocoTestCoverageVerification jibBuildTar"
	docker load --input build/jib-image.tar

.PHONY: aws-login
aws-login:
	@eval $(shell aws ecr get-login --no-include-email --region us-east-1)

.PHONY: push
push: aws-login
	docker tag $(CONTAINER_NAME) $(ECR_REPOSITORY)/$(CONTAINER_NAME):$(TAG)
	docker push $(ECR_REPOSITORY)/$(CONTAINER_NAME):$(TAG)

# Return host CPU architecture, this used to determine the correct docker image to use with the jib plugin
# Intel/AMD CPUs: x86_64=amd64	ARM: arm=arm64  Others: return uname -m
.PHONY: get-platform-architecture
get-platform-architecture:

ifeq "$(PLATFORM_ARCHITECTURE)" "arm"
	@echo "arm64"
else ifeq "$(PLATFORM_ARCHITECTURE)" "x86_64"
	@echo "amd64"
else
	@echo "$(PLATFORM_ARCHITECTURE)"
endif
