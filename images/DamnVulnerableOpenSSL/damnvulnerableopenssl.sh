#!/bin/bash
git clone https://github.com/jurajsomorovsky/DamnVulnerableOpenSSL.git
echo "Building: DamnVulnerableOpenSSL"
cd DamnVulnerableOpenSSL
docker build --build-arg VERSION=1.0 -t damnvulnerableopenssl-server -f Dockerfile .
