#!/usr/bin/env bash
./mvnw clean package
java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch -Xmx32m -jar target/lmax-demo.jar
