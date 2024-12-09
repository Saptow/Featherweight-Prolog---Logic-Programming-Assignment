#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Usage: $0 <file-path> [--trace]"
  exit 1
fi

FILE_PATH=$1
TRACE_FLAG=$2


if [ "$TRACE_FLAG" == "--trace" ]; then
  java interpreter.Main "$FILE_PATH" "$TRACE_FLAG"
else
  java interpreter.Main "$FILE_PATH"
fi