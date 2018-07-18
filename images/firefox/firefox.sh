#!/bin/bash

echo "Building Firefox Images"

docker build --build-arg VERSION=61.0.1 -t ubuntu-firefox-61.0.1-client --target firefox-client .
