# Dev Environment Setup Instructions

# Follow these steps to set up the development environment using Docker Compose:

# 1. Navigate to the 'docker/dev' directory in your terminal.
cd docker/dev

# 2. Create a shared Docker network for the services to communicate.
docker network create dev_shared_net

# 3. Start the database service.
docker compose -f docker-compose-dev-db.yml --env-file .env.dev up -d

# 4. Start the message broker service.
docker compose -f docker-compose-dev-broker.yml --env-file .env.dev up -d

# 5. Start the application service.
docker compose -f docker-compose-dev-app.yml --env-file .env.dev up --build

6. Logs and Monitoring
To view logs for a specific service, use the following command:
docker compose -f docker-compose-dev-app.yml logs -f

# 7. Stopping the Services
To stop all running services, navigate to the 'docker/dev' directory and run:
docker compose -f docker-compose-dev-app.yml down
docker compose -f docker-compose-dev-broker.yml down
docker compose -f docker-compose-dev-db.yml down