#!/bin/bash

docker build -t ocamltls-server --target ocamltls-server .

docker build -t ocamltls-client --target ocamltls-client .
