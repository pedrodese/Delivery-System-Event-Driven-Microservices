.PHONY: help build up down logs restart clean rebuild status test health

GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
RED    := $(shell tput -Txterm setaf 1)
RESET  := $(shell tput -Txterm sgr0)

help:
	@echo ""
	@echo "${GREEN}Delivery System - Makefile Commands${RESET}"
	@echo ""
	@echo "${YELLOW}Build & Deploy:${RESET}"
	@echo "  ${GREEN}make build${RESET}      - Build all services (Maven)"
	@echo "  ${GREEN}make up${RESET}         - Start all containers"
	@echo "  ${GREEN}make down${RESET}       - Stop all containers"
	@echo "  ${GREEN}make rebuild${RESET}    - Build + Start containers"
	@echo ""
	@echo "${YELLOW}Monitoring:${RESET}"
	@echo "  ${GREEN}make logs${RESET}       - View all logs (live)"
	@echo "  ${GREEN}make status${RESET}     - View containers status"
	@echo "  ${GREEN}make health${RESET}     - Check all services health"
	@echo ""
	@echo "${YELLOW}Development:${RESET}"
	@echo "  ${GREEN}make restart service=<name>${RESET} - Restart specific service"
	@echo "  ${GREEN}make test${RESET}       - Run all tests"
	@echo "  ${GREEN}make clean${RESET}      - Stop and remove all containers/volumes"
	@echo ""
	@echo "${YELLOW}Examples:${RESET}"
	@echo "  make restart service=order-service"
	@echo "  make logs service=payment-service"
	@echo ""

build:
	@echo "${GREEN} Building all services...${RESET}"
	@mvn clean package -DskipTests
	@echo "${GREEN} Build completed!${RESET}"

up:
	@echo "${GREEN} Starting all services...${RESET}"
	@cd devops && docker-compose up -d
	@echo "${GREEN} All services started!${RESET}"
	@echo "${YELLOW}📊 Access points:${RESET}"
	@echo "  - API Gateway:    http://localhost:8080"
	@echo "  - Eureka:         http://localhost:8761"
	@echo "  - Config Server:  http://localhost:8888"
	@echo "  - RabbitMQ:       http://localhost:15672 (guest/guest)"

down:
	@echo "${YELLOW} Stopping all services...${RESET}"
	@cd devops && docker-compose down
	@echo "${GREEN} All services stopped!${RESET}"

logs:
	@cd devops && docker-compose logs -f $(if $(service),$(service),)

logs-tail:
	@cd devops && docker-compose logs --tail=100 -f $(if $(service),$(service),)

restart:
	@if [ -z "$(service)" ]; then \
		echo "${YELLOW} Restarting all services...${RESET}"; \
		cd devops && docker-compose restart; \
	else \
		echo "${YELLOW} Restarting $(service)...${RESET}"; \
		cd devops && docker-compose restart $(service); \
	fi
	@echo "${GREEN} Restart completed!${RESET}"

clean:
	@echo "${RED}🧹 Cleaning all containers, volumes and images...${RESET}"
	@cd devops && docker-compose down -v
	@docker system prune -f
	@echo "${GREEN} Cleanup completed!${RESET}"

rebuild:
	@echo "${GREEN} Rebuilding all services...${RESET}"
	@mvn clean package -DskipTests
	@cd devops && docker-compose up --build -d
	@echo "${GREEN} Rebuild and start completed!${RESET}"

status:
	@echo "${GREEN}📊 Container Status:${RESET}"
	@cd devops && docker-compose ps

health:
	@echo "${GREEN} Health Check:${RESET}"
	@echo ""
	@echo "${YELLOW}Config Server:${RESET}"
	@curl -s http://localhost:8888/actuator/health | grep -q "UP" && echo "${GREEN} UP${RESET}" || echo "${RED} DOWN${RESET}"
	@echo "${YELLOW}Eureka Server:${RESET}"
	@curl -s http://localhost:8761/actuator/health | grep -q "UP" && echo "${GREEN} UP${RESET}" || echo "${RED} DOWN${RESET}"
	@echo "${YELLOW}Auth Service:${RESET}"
	@curl -s http://localhost:8084/actuator/health | grep -q "UP" && echo "${GREEN} UP${RESET}" || echo "${RED} DOWN${RESET}"
	@echo "${YELLOW}Order Service:${RESET}"
	@curl -s http://localhost:8081/actuator/health | grep -q "UP" && echo "${GREEN} UP${RESET}" || echo "${RED} DOWN${RESET}"
	@echo "${YELLOW}Payment Service:${RESET}"
	@curl -s http://localhost:8082/actuator/health | grep -q "UP" && echo "${GREEN} UP${RESET}" || echo "${RED} DOWN${RESET}"
	@echo "${YELLOW}API Gateway:${RESET}"
	@curl -s http://localhost:8080/actuator/health | grep -q "UP" && echo "${GREEN} UP${RESET}" || echo "${RED} DOWN${RESET}"
	@echo ""

test:
	@echo "${GREEN}Running tests...${RESET}"
	@mvn test
	@echo "${GREEN}Tests completed!${RESET}"

rabbitmq:
	@echo "${GREEN}🐰 Opening RabbitMQ Management...${RESET}"
	@open http://localhost:15672 || xdg-open http://localhost:15672 2>/dev/null

eureka:
	@echo "${GREEN}🔍 Opening Eureka Dashboard...${RESET}"
	@open http://localhost:8761 || xdg-open http://localhost:8761 2>/dev/null

stats:
	@cd devops && docker-compose stats

backup:
	@echo "${GREEN}💾 Creating database backups...${RESET}"
	@mkdir -p backups
	@docker exec orders-postgres pg_dump -U user ordersdb > backups/ordersdb-$(shell date +%Y%m%d-%H%M%S).sql
	@docker exec auth-postgres pg_dump -U user authdb > backups/authdb-$(shell date +%Y%m%d-%H%M%S).sql
	@docker exec payments-postgres pg_dump -U user paymentsdb > backups/paymentsdb-$(shell date +%Y%m%d-%H%M%S).sql
	@echo "${GREEN} Backups created in ./backups/${RESET}"

dev-infra:
	@echo "${GREEN}🔧 Starting only infrastructure...${RESET}"
	@cd devops && docker-compose up -d postgres-orders postgres-auth postgres-payments rabbitmq
	@echo "${GREEN} Infrastructure started!${RESET}"

dev-stop:
	@echo "${YELLOW} Stopping applications (keeping infrastructure)...${RESET}"
	@cd devops && docker-compose stop config-server eureka-server auth-service order-service payment-service api-gateway
	@echo "${GREEN} Applications stopped!${RESET}"