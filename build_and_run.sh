#!/usr/bin/env bash
mvn package; java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -jar target/jug-vertx-1.0-SNAPSHOT-fat.jar
