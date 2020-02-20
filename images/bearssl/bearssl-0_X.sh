#!/bin/bash

docker build --build-arg VERSION=4 -t bearssl-0_4-server -f Dockerfile-0_x --target bearssl-server .
docker build --build-arg VERSION=4 -t bearssl-0_4-client -f Dockerfile-0_x --target bearssl-client .

docker build --build-arg VERSION=5 -t bearssl-0_5-server -f Dockerfile-0_x --target bearssl-server .
docker build --build-arg VERSION=5 -t bearssl-0_5-client -f Dockerfile-0_x --target bearssl-client .
