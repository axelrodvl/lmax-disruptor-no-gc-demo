#!/usr/bin/env bash
./mvnw clean package

# Note
# --add-exports and --add-opens are required by Chronicle (https://chronicle.software/chronicle-support-java-17/)
java \
  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch \
  -Xmx256m \
  --add-exports=java.base/jdk.internal.ref=ALL-UNNAMED \
  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED \
  --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens=java.base/java.io=ALL-UNNAMED \
  --add-opens=java.base/java.util=ALL-UNNAMED \
  -XX:+FlightRecorder -XX:StartFlightRecording=duration=300s,filename=websocket-client-with-disruptor-no-gc.jfr \
  -XX:NativeMemoryTracking=detail \
  -jar target/websocket-client-with-disruptor-no-gc.jar
