#  Watermark Service #
This is a code challenge for a job application (not a living service).
The Watermark Service provides adding a watermark to a given document like
a book or journal.

## Health ##
You can check the health status of the service by sending a request:
http://server:port/health

## Available Methods ##

/watermark/watermarkPublication/?id=
Watermarks a publication with given ID.

/watermark/ticketStatus/?ticketId=
Gets the ticket status by given ticket ID that shows if the watermarking
has finished.

/watermark/getPublication/?ticketId=
Gets the publication by given ticket ID. Used when the ticket status
is FINISHED.

## Documentation ##

See directory "documentation" for Javadoc files and modelling drawings like
class diagram, sequence diagrams and status flow chart.

## Data Structure ##

Book:

document {
    "title": String,
    "author": String,
    "content": BOOK,
    "topic": String,
    "watermark" {
        "title": String,
        "author": String,
        "content": BOOK,
        "topic": String
    }
}

Journal:

document {
    "title": String,
    "author": String,
    "content": JOURNAL,
    "watermark" {
        "title": String,
        "author": String,
        "content": BOOK,
    }
}


##  Prerequisites ##
- Local ElasticSearch in Version 2.4.4
- Configure the Elastic Search Cluster in the application.properties at spring.data.elasticsearch.cluster-name
- Start the Elastic Search Cluster

## Tests ##

See tests in src/test directory:

- Unittests for preparation, queueing and watermarking classes
- Integrationtests for WatermarkRestController


## Necessary configuration ##
- Please configure the used elasticsearch cluster in the application.properties file.


## Open Tasks for further development ##
- Mock calls in integrationtests (currently the real elasticsearch instance is used)
- Methods for the following cases:
    - get all publications that are not watermarked yet
    - get all tickets that are in status pending or failed
- Avoid duplicates of publications and tickets in the elasticsearch
- Performance tests to answer if producer and consumer work without concurrency issues
- Build (docker) container for running as own image in a prepared environment





