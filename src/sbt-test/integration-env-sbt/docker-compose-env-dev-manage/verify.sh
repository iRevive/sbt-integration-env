#!/usr/bin/env bash

# env alive
if [ -z "$(docker ps -q -f "name=dev-it-env")" ]; then
  exit 1
else
  exit 0
fi
