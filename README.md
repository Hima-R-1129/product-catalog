# Product Catalog - Spring Boot (H2)

A simple product catalog backend built with Spring Boot and H2 database. Provides CRUD, search, health check, Dockerfile, GitHub Actions CI, and Kubernetes YAML.

## Features
- CRUD endpoints for products
- Search by name or category: `GET /products/search?query=` (searches name and category)
- Health check: `GET /health`
- H2 in-memory database (persistent file optional)
- Dockerfile and GitHub Actions pipeline
- Kubernetes manifests (optional)

## API Endpoints
- `GET /products` - list all products
- `GET /products/{id}` - get product by id
- `POST /products` - create product
- `PUT /products/{id}` - update product
- `DELETE /products/{id}` - delete product
- `GET /products/search?query=...` - search by name or category
- `GET /actuator/health` - health check
- `GET /actuator/metrics/endpoint.failure.count` - Failed Metrics
- `GET /actuator/metrics/endpoint.success.count` - Success Metrics


### Product JSON
```json
{
  "name": "Product A",
  "description": "A nice product",
  "price": 19.99,
  "category": "electronics"
}
```

## Run locally
Requirements: Java 17+, Maven

```bash
# build
mvn clean package -DskipTests

# run
java -jar target/product-catalog-0.0.1-SNAPSHOT.jar
```

App runs on port 8080 by default. H2 console available at `http://localhost:8080/h2-console` (jdbc url: `jdbc:h2:mem:productdb`).

## Docker
```bash
# build image
docker build -t product-catalog:latest .

# run
docker run -p 8080:8080 product-catalog:latest
```

## CI (GitHub Actions)
Included workflow `./github/workflows/ci.yml` builds, tests, and builds Docker image (container build) — adjust publish step to push to your registry.

## Deploy (manual)
1. Build and push Docker image to your container registry.
2. Apply Kubernetes manifests in `k8s/` or deploy to your chosen cloud (ECS/AKS/etc.)

## Notes
- This project uses H2 in-memory DB for simplicity. Switch to Postgres by updating `application.properties` and dependencies.