#!/bin/sh
exec docker build --squash --tag alpine-build .
