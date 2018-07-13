# transaction-stats-ws

[![Build Status](https://travis-ci.org/landpro/transaction-stats-ws.svg?branch=master)](https://travis-ci.org/landpro/transaction-stats-ws)
[![codecov](https://codecov.io/gh/landpro/transaction-stats-ws/branch/master/graph/badge.svg)](https://codecov.io/gh/landpro/transaction-stats-ws)

Transaction statistics web service.

## Functionality

-   registration of transaction: timestamp, amount
-   checking transaction amount statistics: min, max, avg, sum, count
-   statistics period: 60 seconds, configurable

## Non-functional requirements

-   reading endpoint should execute in constant time and memory – O(1)

## API & Limitations

-   REST API
-   no authentication
-   in-memory solution – data is forgotten on server restart

## Technologies

-   Java 8
-   Spring Boot
-   Jackson JSON
-   Maven

## Build

-   build: `mvn clean package`

## REST Endpoints

Description of API endpoints with examples of request / response.

### Register Transaction

POST /transactions

#### Request
```
curl -X "POST" "http://localhost:8080/transactions" \
     -H 'Content-Type: application/json' \
     -d $'{
  "amount": 900.5,
  "timestamp": 1531438743973
}'
```
#### Response

-   When registered:
```
HTTP/1.1 201 
```

-   When ignored as out of scope:
```
HTTP/1.1 204 
```

### Get Statistics

GET /statistics

```
curl "http://localhost:8080/statistics" \
     -H 'Content-Type: application/json' \
     -d $'{
  "amount": 12.3,
  "timestamp": 1478192204000
}'
```
#### Response
```
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8

{
  "sum": 6187,
  "avg": 1546.75,
  "max": 4343,
  "min": 43,
  "count": 4
}
```
