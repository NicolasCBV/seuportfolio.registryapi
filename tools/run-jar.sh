export PORT=8080
export APP_NAME=registryapi
export DB_HOSTNAME=localhost
export DB_PORT=8888
export DB_PATH=db
export DB_USERNAME=postgres
export DB_PASSWORD=password
export CORS_MAPPING='/**'
export CORS_CLIENT_URL=*
export ACCESS_TOKEN_SECRET=asopdfoasfdoajsdfOPJoihjsfd
export ACCESS_TOKEN_EXP=2

export REFRESH_TOKEN_SECRET=sdojfoasjfoasidjfojew
export REFRESH_TOKEN_EXP=24
export REFRESH_TOKEN_SECURE=true
export REFRESH_TOKEN_DOMAIN=localhost
export REFRESH_TOKEN_HTTP_ONLY=true

export SHOW_SQL=true
export EXPOSE_HEALTH=health

java -Dspring.profiles.active=prod -jar ./target/registryapi-0.0.1-SNAPSHOT.jar
