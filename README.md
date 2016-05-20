# prometheus-cloudwatch-proxy

[![Build Status](https://travis-ci.org/nlindblad/prometheus-cloudwatch-proxy.svg?branch=master
)](https://travis-ci.org/nlindblad/prometheus-cloudwatch-proxy)

Understands the `PutMetricData` action in the [Amazon Web Services CloudWatch API](http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_PutMetricData.html) (`2010-08-01` format) and exposes it to [Prometheus](http://prometheus.io).

    +------------+
    |            +-----------+
    | Application|           |
    |     A      |      +----v---------+     +---------------+
    |            |      |              |     |               |
    +------------+      |  Prometheus  |     |  Prometheus   |
                        |  CloudWatch  +----->    server     |
    +------------+      |    Proxy     |     |               |
    |            |      |              |     |               |
    | Application|      +----^---------+     +---------------+
    |     B      |           |
    |            +-----------+
    +------------+

## Configure AWS SDK

```
val client = new AmazonCloudWatchAsyncClient()
client.setEndpoint("http://127.0.0.1:8080/cloudwatch")
```
