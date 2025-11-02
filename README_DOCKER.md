# Docker deployment

## Files
- `Dockerfile`: multi-stage build (Maven -> JRE)
- `docker-compose.yml`: runs MySQL + the app
- `.env.example`: copy to `.env` and adjust passwords/ports

## Quick start (local or server)
```bash
cp .env.example .env
# edit .env to set secure passwords

docker compose build
docker compose up -d
```

App will be available at: http://<host>:${APP_PORT}  (default http://localhost:8090)

## Logs
```bash
docker compose logs -f app
docker compose logs -f db
```

## Updating the app
```bash
docker compose build app
docker compose up -d app
```

## Notes
- The app reads DB settings from environment variables.
- MySQL data persists in the `mysql_data` volume.
- Set a strong `MYSQL_ROOT_PASSWORD` before going to production.
- To expose on the internet with a domain + HTTPS, put a reverse proxy like Traefik/Caddy/nginx in front.
```
