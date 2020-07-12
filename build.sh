#!/usr/bin/env bash

DOCKER_BUILDKIT=1 docker build -t deployment-dashboard .
id=$(docker create deployment-dashboard)
docker cp $id:/home/gradle/deployment-dashboard.zip deployment-dashboard.zip
docker rm -v $id
