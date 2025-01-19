#!/bin/bash

git clone RUN git clone https://github.com/jar-black/Geoevent-api.git
# shellcheck disable=SC2164
cd Geoevent-api

docker-compose up -d --build
