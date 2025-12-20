# Dev Environment Setup Instructions

## Follow these steps to set up the homologation environment using Docker Compose:

### 1. Navigate to the 'docker/hom' directory in your terminal.
cd docker/hom

### 2. Create a shared Docker network for the services to communicate.
docker network create hom_network

### 3. Start the homologation application services using Docker Compose.
docker compose -f docker-compose-hom-app.yml --env-file .env.hom up -d

## Log & Monitoring
To view logs for the homologation application service, use the following command:
docker compose -f docker-compose-hom-app.yml logs -f

### 4. Stopping the Services
To stop all running services, navigate to the 'docker/hom' directory and run:
docker compose -f docker-compose-hom-app.yml down
docker compose -f docker-compose-hom-db.yml down
docker compose -f docker-compose-hom-broker.yml down