#!/usr/bin/env bash

if [ "$RELEASE" = true ]; then
  type="release"
else
  type="snapshot"
fi
echo "Building plugin (type '$type')..."

DOCKER_BUILDKIT=1 docker build --build-arg RELEASE=$RELEASE -t deployment-dashboard .
id=$(docker create deployment-dashboard)
rm deployment-dashboard.zip
docker cp $id:/home/gradle/deployment-dashboard.zip deployment-dashboard.zip
docker rm -v $id

echo "Done. Copy 'deployment-dashboard.zip' to the '<TeamCityData>/plugins/' folder and reload from the plugins page"
