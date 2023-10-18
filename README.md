# Kotlin Multiplatform gRPC Client

Support target:
![badge][badge-linux]
![badge][badge-windows]
![badge][badge-mac]

## Overview

This is a Kotlin Multiplatform gRPC client library based on [wire](https://github.com/square/wire) and [ktor](https://github.com/ktorio/ktor).

## Usage

Run the demo command to create a topic on RocketMQ broker via gRPC protocol. Download the binary from [releases](https://github.com/ShadowySpirits/kotlin-native-grpc/releases/tag/0.1.0) or build yourself.

```shell
./mqadmin -h
Usage: mqadmin [<options>] <command> [<args>]...

Options:
  --endpoint=<text>  Endpoint of RocketMQ broker
  -h, --help         Show this message and exit

Commands:
  create-topic  Create a new topic
```

## Build

Requirement: libcurl-dev

```shell
./gradlew assemble
```

[badge-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat 
[badge-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-mac]: http://img.shields.io/badge/-macos-111111.svg?style=flat
