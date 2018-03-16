#!/bin/sh
exec docker build --tag alpine-build .

#squash does not work on ubuntu
