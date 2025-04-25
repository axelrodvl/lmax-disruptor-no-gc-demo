#!/usr/bin/env bash
./mvnw clean package
java \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch \
  -Xmx64m \
  -XX:+FlightRecorder -XX:StartFlightRecording=duration=240s,filename=websocket-client-with-disruptor-no-gc.jfr \
  -jar target/websocket-client-with-disruptor-no-gc.jar
