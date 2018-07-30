#!/bin/bash

shopt -s globstar

if ! [[ -d bin ]]; then mkdir bin; fi

javac -classpath src/:lib/gson-2.8.5.jar -d bin/ src/**/*.java

#jar -cvfm Crawler.jar Manifest.txt -C bin .

#jar -uvf Crawler.jar -C src .
