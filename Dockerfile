FROM node:12.18-stretch-slim AS ui

WORKDIR build

# Tests currently require London timezone
ENV TZ=Europe/London
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Tests currently require London locale
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y locales && \
    sed -i -e 's/# en_GB.UTF-8 UTF-8/en_GB.UTF-8 UTF-8/' /etc/locale.gen && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=en_GB.UTF-8
ENV LANG en_GB.UTF-8

# Download dependencies
COPY frontend/package.json ./
RUN npm install

# Run tests
COPY frontend ./
RUN export CI=true && \
    # Use full timezone data from the full-icu package
    export NODE_ICU_DATA=node_modules/full-icu && \
    npm test

# Run build, with output in build/*
RUN npm run build


FROM gradle:6.5-jdk8 AS server

COPY ./ ./
COPY --from=ui build/build/* ./server/src/main/resources/buildServerResources/

RUN gradle --no-daemon test serverPlugin
RUN mv server/build/distributions/server.zip ./deployment-dashboard.zip
