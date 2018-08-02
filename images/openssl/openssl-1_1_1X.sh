#!/bin/bash
docker build --build-arg VERSION="1.1.1-pre2" -t openssl-1_1_1-pre2-server -f Dockerfile-1_1_1_pre2 --target openssl-server .
docker build --build-arg VERSION="1.1.1-pre2" -t openssl-1_1_1-pre2-client -f Dockerfile-1_1_1_pre2 --target openssl-client .

