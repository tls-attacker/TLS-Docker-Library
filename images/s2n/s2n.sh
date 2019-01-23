#!/bin/bash

docker build --build-arg VERSION=latest -t s2n-latest-server --target s2n-server .
docker build --build-arg VERSION=latest -t s2n-latest-client --target s2n-client .

docker build --build-arg VERSION=fips -t s2n-fips-server -f Dockerfile_fips --target s2n-server .
docker build --build-arg VERSION=fips -t s2n-fips-client -f Dockerfile_fips --target s2n-client .

