#!/bin/bash

docker build -t ocamltls-0.8.0-server --target ocamltls-server .

docker build -t ocamltls-0.8.0-client --target ocamltls-client .
