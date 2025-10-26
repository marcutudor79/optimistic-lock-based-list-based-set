#!/bin/bash
cd ../deps

for a in *.tar.gz; do
  case "$a" in
    *.tar.gz|*.tgz) echo "extracting $a"; tar -xzf "$a" ;;
  esac
done

export JAVA_HOME=$(pwd)/jdk1.7.0_80
export PATH=$JAVA_HOME/bin:$PATH

export ANT_HOME=$(pwd)/apache-ant-1.9.15
export PATH=$ANT_HOME/bin:$PATH

cd ../code


