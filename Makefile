.PHONY: help infra-up infra-down infra-logs backend-run backend-test admin-run check
.PHONY: backend-build backend-docker integration-test foundation-check
.PHONY: identity-test auth-register auth-login

help:
	@echo "KÓLÉ development commands"
	@echo "make infra-up       Start PostgreSQL, Redis, MinIO and Mailpit"
	@echo "make infra-down     Stop local infrastructure"
	@echo "make infra-logs     Show infrastructure logs"
	@echo "make backend-run    Start Spring Boot backend"
	@echo "make backend-test   Run backend tests"
	@echo "make admin-run      Start admin dashboard"
	@echo "make check          Run baseline project checks"
	
identity-test:
	cd backend && ./mvnw \
		-Dtest=AuthFlowIntegrationTest \
		test

auth-register:
	curl -X POST \
		http://localhost:8080/api/v1/auth/register \
		-H "Content-Type: application/json" \
		-d '{"fullName":"Test User","email":"test@example.com","phoneNumber":"+2348012345678","password":"StrongPassword123"}'

auth-login:
	curl -X POST \
		http://localhost:8080/api/v1/auth/login \
		-H "Content-Type: application/json" \
		-d '{"email":"test@example.com","password":"StrongPassword123"}'

infra-up:
	docker compose up -d

infra-down:
	docker compose down

infra-logs:
	docker compose logs -f

backend-build:
	cd backend && ./mvnw clean package

backend-docker:
	docker build -t kole-backend:local backend

integration-test:
	cd backend && ./mvnw clean verify

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

foundation-check:
	docker compose config
	cd backend && ./mvnw clean verify
	docker build -t kole-backend:local backend