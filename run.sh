#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo_step() {
    echo -e "${GREEN}[STEP]${NC} $1"
}

echo_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_java() {
    local java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [[ "$java_version" == "25" ]]; then
        echo_info "Java 25 detectado - configurando Java 21..."
        if [ -d "/usr/lib/jvm/jre-21-openjdk/../" ]; then
            export JAVA_HOME="/usr/lib/jvm/jre-21-openjdk/../"
            export PATH="$JAVA_HOME/bin:$PATH"
        else
            echo_error "Java 21 nao encontrado. Instale ou configure JAVA_HOME."
            exit 1
        fi
    fi
    echo_info "Java version: $(java -version 2>&1 | head -1)"
}

start_infrastructure() {
    echo_step "Subindo infraestrutura (Docker)..."
    docker compose up -d

    echo_info "Aguardando databases ficarem prontas..."
    sleep 15

    echo_info "Verificando status dos containers..."
    docker compose ps
}

start_services() {
    echo_step "Iniciando servicos em background..."

    mkdir -p logs

    services=(
        "auth-service:8086"
        "card-service:8080"
        "deck-service:8081"
        "card-creator-service:8083"
        "proxy-service:8085"
        "community-service:8087"
        "gateway-service:8088"
    )

    for service in "${services[@]}"; do
        name="${service%%:*}"
        port="${service##*:}"

        echo_info "Iniciando $name na porta $port..."

        nohup ./gradlew :${name}:bootRun > "logs/${name}.log" 2>&1 &
        echo $! > "logs/${name}.pid"
    done

    echo_step "Todos os servicos iniciados!"
    echo ""
    echo_info "PIDs salvos em logs/*.pid"
    echo_info "Logs em logs/*.log"
}

wait_for_services() {
    echo_step "Aguardando servicos ficarem prontos..."

    for service in auth-service card-service deck-service card-creator-service proxy-service community-service gateway-service; do
        port=$(case $service in
            auth-service) echo 8086 ;;
            card-service) echo 8080 ;;
            deck-service) echo 8081 ;;
            card-creator-service) echo 8083 ;;
            proxy-service) echo 8085 ;;
            community-service) echo 8087 ;;
            gateway-service) echo 8088 ;;
        esac)

        echo_info "Aguardando $service (porta $port)..."
        for i in {1..60}; do
            if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
                echo_info "$service pronto!"
                break
            fi
            if [ $i -eq 60 ]; then
                echo_error "$service nao ficou pronto apos 60 tentativas."
                echo_info "Verifique logs/${service}.log"
            fi
            sleep 2
        done
    done
}

show_status() {
    echo ""
    echo_step "========== STATUS DOS SERVICOS =========="
    echo ""

    services=(
        "auth-service:8086"
        "card-service:8080"
        "deck-service:8081"
        "card-creator-service:8083"
        "proxy-service:8085"
        "community-service:8087"
        "gateway-service:8088"
    )

    for service in "${services[@]}"; do
        name="${service%%:*}"
        port="${service##*:}"

        if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo -e "${GREEN}[UP]${NC}   $name (porta $port)"
        else
            echo -e "${RED}[DOWN]${NC} $name (porta $port)"
        fi
    done

    echo ""
    echo_step "=========================================="
}

show_help() {
    echo "Uso: ./run.sh [comando]"
    echo ""
    echo "Comandos:"
    echo "  start       - Sobe infraestrutura + todos os servicos (padrao)"
    echo "  infra       - Sobe apenas infraestrutura (Docker)"
    echo "  services    - Inicia apenas os servicos (sem Docker)"
    echo "  stop        - Para todos os servicos"
    echo "  logs        - Mostra logs de todos os servicos"
    echo "  status      - Mostra status dos servicos"
    echo "  restart     - Reinicia todos os servicos"
    echo "  clean       - Para servicos e remove logs"
    echo "  help        - Mostra esta ajuda"
    echo ""
}

stop_services() {
    echo_step "Parando servicos..."

    if [ -d "logs" ]; then
        for pidfile in logs/*.pid; do
            if [ -f "$pidfile" ]; then
                name=$(basename "$pidfile" .pid)
                pid=$(cat "$pidfile")

                if kill -0 "$pid" 2>/dev/null; then
                    echo_info "Parando $name (PID: $pid)..."
                    kill "$pid" 2>/dev/null || true
                fi
            fi
        done
    fi

    pkill -f "gradlew.*bootRun" 2>/dev/null || true
    echo_step "Servicos parados!"
}

show_logs() {
    if [ -d "logs" ]; then
        for logfile in logs/*.log; do
            if [ -f "$logfile" ]; then
                name=$(basename "$logfile" .log)
                echo ""
                echo "========== LOG: $name =========="
                tail -50 "$logfile"
            fi
        done
    else
        echo_error "Nenhum log encontrado. Execute './run.sh start' primeiro."
    fi
}

clean() {
    stop_services
    echo_step "Removendo logs..."
    rm -rf logs
    echo_step "Limpeza concluida!"
}

restart_services() {
    stop_services
    sleep 2
    start_services
    wait_for_services
    show_status
}

case "${1:-start}" in
    start)
        check_java
        start_infrastructure
        start_services
        wait_for_services
        show_status
        ;;
    infra)
        start_infrastructure
        ;;
    services)
        check_java
        start_services
        wait_for_services
        show_status
        ;;
    stop)
        stop_services
        ;;
    logs)
        show_logs
        ;;
    status)
        show_status
        ;;
    restart)
        check_java
        restart_services
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo_error "Comando desconhecido: $1"
        show_help
        exit 1
        ;;
esac
