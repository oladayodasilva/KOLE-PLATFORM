.PHONY: help infra-up infra-down infra-logs backend-run backend-test admin-run check

help:
	@echo "KÓLÉ development commands"
	@echo "make infra-up       Start PostgreSQL, Redis, MinIO and Mailpit"
	@echo "make infra-down     Stop local infrastructure"
	@echo "make infra-logs     Show infrastructure logs"
	@echo "make backend-run    Start Spring Boot backend"
	@echo "make backend-test   Run backend tests"
	@echo "make admin-run      Start admin dashboard"
	@echo "make check          Run baseline project checks"

infra-up:
	docker compose up -d

infra-down:
	docker compose down

infra-logs:
	docker compose logs -f

backend-run:
	cd backend && ./mvnw spring-boot:run

backend-test:
	cd backend && ./mvnw clean test

admin-run:
	cd admin && npm run dev

check:
	cd backend && ./mvnw clean test
	cd admin && npm run lint
	cd admin && npm run build
	cd apps/customer_app && flutter analyze && flutter test
	cd apps/collector_app && flutter analyze && flutter test