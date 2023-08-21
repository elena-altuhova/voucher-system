FROM postgres:alpine3.18

LABEL description="Postgres DB"
LABEL version="1.0"

COPY src/main/resources/dbscripts/*.sql /docker-entrypoint-initdb.d/