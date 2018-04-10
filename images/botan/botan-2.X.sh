#!/bin/bash

docker build --build-arg VERSION=0 -t botan-2_0_0-server -f Dockerfile-2_0_x .
docker build --build-arg VERSION=1 -t botan-2_0_1-server -f Dockerfile-2_0_x .

docker build --build-arg VERSION=0 -t botan-2_1_0-server -f Dockerfile-2_1_x .

docker build --build-arg VERSION=0 -t botan-2_2_0-server -f Dockerfile-2_2_x .

docker build -t botan-2_3_0-server -f Dockerfile-2_3_0 .