FROM postgres:11-alpine as dumper

ENV POSTGRES_DB=db
ENV PG_USER=postgres
ENV POSTGRES_PASSWORD=password
ENV PGDATA=/data

COPY ./migration/*.sql /docker-entrypoint-initdb.d/

RUN sed -i "s/exec \"\$@\"/echo \"skipping...\"/" "/usr/local/bin/docker-entrypoint.sh" && \
    /usr/local/bin/docker-entrypoint.sh postgres

FROM postgres:11-alpine

COPY --from=dumper /data $PGDATA
VOLUME [ "/data" ]
