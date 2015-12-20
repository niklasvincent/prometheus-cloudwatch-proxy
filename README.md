# prometheus-cloudwatch-proxy

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
