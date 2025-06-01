#!/usr/bin/env bash

curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.16.0/cloud-sql-proxy.linux.amd64
chmod +x cloud-sql-proxy
mv cloud-sql-proxy /usr/local/bin
