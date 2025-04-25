#!/usr/bin/env bash
./mvnw clean package
java \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch \
  -Xmx64m \
  -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=websocket-client-with-disruptor-no-gc.jfr \
  -XX:NativeMemoryTracking=detail \
  -jar target/websocket-client-with-disruptor-no-gc.jar
