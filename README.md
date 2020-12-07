<div align="center">
  <br/>
  <img src="./docs/images/joupon-logo.png" width="400"/>
  <br/>
  <br/>
  <p>
    Installable coupon managing service
  </p>
  <p>
    <a href="https://github.com/pjongy/joupon/blob/master/LICENSE">
      <img src="https://img.shields.io/badge/license-MIT-blue.svg"/>
    </a>
  </p>
</div>

---

## API specification
[API specification document](./API.README.md)


## Quick start (on local)

### Pre-requisites
- docker >= 19.03.8
- docker-compose >= 1.25.5

```
$ docker-compose -f local-docker-compose.yml up -d
```

## Project structure
```
/
  /src/main/
    /kotlin
      /com/github/pjongy
    /resource
```

### Pre-requisite
- openjdk 12
- Run mysql server and create database
    ```
    $ docker run -d -e  MYSQL_ROOT_PASSWORD={..mysql password..} -p 3306:3306 mysql
    ```

## Build
```
$ docker build . -f ./Dockerfile
```

## Start
```
$ docker run \
 -e APP_CONFIG=local \
 -e JOUPON__MYSQL__JDBC_URL={..mysql connection jdbc url..} \
 -e JOUPON__MYSQL__USER={..mysql user..} \
 -e JOUPON__MYSQL__PASSWORD={..mysql password..} \
 -e JOUPON__INTERNAL_API_KEYS={..comma separated internal access keys..} \
 -p 80:8080\
 pjongy/joupon
```
