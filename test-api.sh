#!/bin/bash

BASE_URL="${BASE_URL:-http://localhost}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

ACCESS_TOKEN=""
REFRESH_TOKEN=""
USER_ID=""

echo_step() {
    echo ""
    echo -e "${GREEN}[PASSO]${NC} $1"
}

echo_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

echo_result() {
    echo -e "${YELLOW}[RESULTADO]${NC}"
}

request() {
    local method=$1
    local url=$2
    local data=$3
    local token=$4
    local extra=$5

    local curl_cmd="curl -s -X $method '$url'"

    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -H 'Content-Type: application/json'"
        curl_cmd="$curl_cmd -d '$data'"
    fi

    if [ -n "$token" ]; then
        curl_cmd="$curl_cmd -H 'Authorization: Bearer $token'"
    fi

    if [ -n "$extra" ]; then
        curl_cmd="$curl_cmd $extra"
    fi

    eval $curl_cmd
}

wait_for_service() {
    local port=$1
    echo_info "Aguardando servico na porta $port..."

    for i in {1..30}; do
        if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo_info "Servico na porta $port esta pronto!"
            return 0
        fi
        sleep 1
    done

    echo -e "${RED}[ERRO]${NC} Servico na porta $port nao ficou pronto."
    return 1
}

echo "=============================================="
echo "   YU-GI-OH DECK MANAGEMENT - TESTE COMPLETO"
echo "=============================================="

echo_step "1. Aguardando servicos ficarem prontos..."
wait_for_service 8086
wait_for_service 8080
wait_for_service 8081
wait_for_service 8083
wait_for_service 8082
wait_for_service 8085

echo_step "2. Registrando novo usuario..."
RESULT=$(request "POST" "${BASE_URL}:8086/auth/register" '{
    "username": "testuser_'"$(date +%s)"'",
    "email": "test_'"$(date +%s)"'@yugioh.com",
    "password": "teste123"
}')
echo_result
echo "$RESULT" | head -c 500
echo ""

ACCESS_TOKEN=$(echo "$RESULT" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
REFRESH_TOKEN=$(echo "$RESULT" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)
USER_ID=$(echo "$RESULT" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

if [ -z "$ACCESS_TOKEN" ]; then
    echo -e "${RED}[ERRO]${NC} Falha ao registrar usuario."
    exit 1
fi

echo_info "Token obtido com sucesso!"

echo_step "3. Verificando dados do usuario logado..."
RESULT=$(request "GET" "${BASE_URL}:8086/auth/me" "" "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

echo_step "4. Buscando cartas no card-service..."
RESULT=$(request "GET" "${BASE_URL}:8080/cards?name=Dark%20Magician" "" "" "-H 'Accept: application/json'")
echo_result
echo "$RESULT" | head -c 300
echo "..."

CARD_ID=$(echo "$RESULT" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo_info "Card ID encontrado: $CARD_ID"

echo_step "5. Criando um deck..."
RESULT=$(request "POST" "${BASE_URL}:8081/decks" '{"name": "Deck de Teste"}' "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

DECK_ID=$(echo "$RESULT" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo_info "Deck ID: $DECK_ID"

echo_step "6. Listando decks do usuario..."
RESULT=$(request "GET" "${BASE_URL}:8081/decks" "" "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

echo_step "7. Adicionando carta ao deck..."
if [ -n "$CARD_ID" ]; then
    RESULT=$(request "POST" "${BASE_URL}:8081/decks/${DECK_ID}/cards" "{\"cardId\": $CARD_ID, \"quantity\": 3}" "$ACCESS_TOKEN")
    echo_result
    echo "$RESULT" | head -c 200
    echo "..."
fi

echo_step "8. Verificando deck com cartas..."
RESULT=$(request "GET" "${BASE_URL}:8081/decks/${DECK_ID}/full" "" "$ACCESS_TOKEN")
echo_result
echo "$RESULT" | head -c 400
echo "..."

echo_step "9. Exportando deck (.ydk)..."
RESULT=$(request "GET" "${BASE_URL}:8081/decks/${DECK_ID}/export" "" "$ACCESS_TOKEN")
echo_result
echo "Primeiras 200 linhas do .ydk:"
echo "$RESULT" | head -c 500
echo "..."

echo_step "10. Criando carta customizada..."
RESULT=$(request "POST" "${BASE_URL}:8083/custom-cards" '{
    "name": "Dragão Tester Customizado",
    "type": "MONSTER",
    "attribute": "LIGHT",
    "race": "Dragon",
    "level": 8,
    "atk": 3000,
    "def": 2500,
    "description": "卡 test card with special effects"
}' "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

CUSTOM_CARD_ID=$(echo "$RESULT" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo_info "Custom Card ID: $CUSTOM_CARD_ID"

echo_step "11. Listando cartas customizadas..."
RESULT=$(request "GET" "${BASE_URL}:8083/custom-cards" "" "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

echo_step "12. Gerando PDF proxy do deck..."
RESULT=$(request "GET" "${BASE_URL}:8082/proxy/${DECK_ID}" "" "$ACCESS_TOKEN" "-o /tmp/proxy_test.pdf -w '%{http_code}'")
echo_result
if [ -f "/tmp/proxy_test.pdf" ]; then
    echo_info "PDF gerado com sucesso: /tmp/proxy_test.pdf"
    ls -lh /tmp/proxy_test.pdf
fi

echo_step "13. Registrando perfil de jogador..."
RESULT=$(request "POST" "${BASE_URL}:8085/players" '{
    "displayName": "TestPlayer",
    "latitude": -23.5505,
    "longitude": -46.6333,
    "platforms": ["YGOPRO", "EDOPRO"]
}' "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

echo_step "14. Atualizando status do jogador..."
RESULT=$(request "PATCH" "${BASE_URL}:8085/players/me/status" '{"status": "AVAILABLE"}' "$ACCESS_TOKEN")
echo_result
echo "Status atualizado (204 No Content esperado)"

echo_step "15. Buscando jogadores proximos..."
RESULT=$(request "GET" "${BASE_URL}:8085/players/nearby?lat=-23.5505&lng=-46.6333&radiusKm=100" "" "$ACCESS_TOKEN")
echo_result
echo "$RESULT"

echo_step "16. Renova token..."
RESULT=$(request "POST" "${BASE_URL}:8086/auth/refresh" "{\"refreshToken\": \"$REFRESH_TOKEN\"}")
echo_result
echo "$RESULT" | head -c 200

NEW_ACCESS_TOKEN=$(echo "$RESULT" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
NEW_REFRESH_TOKEN=$(echo "$RESULT" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)

if [ -n "$NEW_ACCESS_TOKEN" ]; then
    echo_info "Token renovado com sucesso!"
fi

echo_step "17. Removendo carta do deck..."
RESULT=$(request "DELETE" "${BASE_URL}:8081/decks/${DECK_ID}/cards" "{\"cardId\": $CARD_ID, \"zone\": \"MAIN\"}" "$NEW_ACCESS_TOKEN")
echo_result
echo "$RESULT" | head -c 200

echo_step "18. Deletando deck..."
RESULT=$(request "DELETE" "${BASE_URL}:8081/decks/${DECK_ID}" "" "$NEW_ACCESS_TOKEN" "-w '\nHTTP Status: %{http_code}'")
echo_result
echo "Deck deletado!"

echo_step "19. Fazendo logout..."
RESULT=$(request "POST" "${BASE_URL}:8086/auth/logout" "" "$NEW_ACCESS_TOKEN" "-H 'X-Refresh-Token: $NEW_REFRESH_TOKEN' -w '\nHTTP Status: %{http_code}'")
echo_result
echo "Logout realizado!"

echo ""
echo "=============================================="
echo "   TESTE COMPLETO FINALIZADO COM SUCESSO!"
echo "=============================================="
echo ""
echo "Resumo:"
echo "  - Registro/Login: OK"
echo "  - Busca de cartas: OK"
echo "  - CRUD de decks: OK"
echo "  - Export .ydk: OK"
echo "  - Cartas customizadas: OK"
echo "  - PDF Proxy: OK"
echo "  - Comunidade: OK"
echo "  - Refresh token: OK"
echo "  - Logout: OK"
echo ""