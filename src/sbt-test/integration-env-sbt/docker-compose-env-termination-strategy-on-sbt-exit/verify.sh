#!/usr/bin/env bash

# env stopped
if [ -z "$(docker ps -q -f "name=ci-it-env")" ]; then
  exit 0
else
  exit 1
fi
