#!/bin/bash

BASE="card-creator-service"
JAVA="$BASE/src/main/java/com/odevpedro/yugiohcollections/creator"
RESOURCES="$BASE/src/main/resources"

# Diretórios
mkdir -p "$JAVA/adapter/in/rest"
mkdir -p "$JAVA/adapter/out/messaging"
mkdir -p "$JAVA/adapter/out/persistence/entity"
mkdir -p "$JAVA/adapter/out/persistence/repository"
mkdir -p "$JAVA/application/dto"
mkdir -p "$JAVA/application/mapper"
mkdir -p "$JAVA/application/service"
mkdir -p "$JAVA/config"
mkdir -p "$JAVA/domain/model/enums"
mkdir -p "$JAVA/domain/model/ports"
mkdir -p "$RESOURCES/db/migration"

# Arquivos raiz
touch "$BASE/build.gradle"
touch "$BASE/settings.gradle"
touch "$BASE/docker-compose.yml"

# Resources
touch "$RESOURCES/application.yml"
touch "$RESOURCES/db/migration/V1__init_schema.sql"

# Main class
touch "$JAVA/CardCreatorApplication.java"

# Config
touch "$JAVA/config/GlobalExceptionHandler.java"

# Domain
touch "$JAVA/domain/model/CustomCard.java"
touch "$JAVA/domain/model/CardCreationException.java"
touch "$JAVA/domain/model/enums/CardType.java"
touch "$JAVA/domain/model/enums/CardStatus.java"
touch "$JAVA/domain/model/enums/MonsterAttribute.java"
touch "$JAVA/domain/model/enums/SpellSubType.java"
touch "$JAVA/domain/model/enums/TrapSubType.java"
touch "$JAVA/domain/model/ports/CustomCardRepositoryPort.java"

# Application
touch "$JAVA/application/dto/CreateCardRequest.java"
touch "$JAVA/application/mapper/CustomCardMapper.java"
touch "$JAVA/application/service/CustomCardService.java"

# Adapter in
touch "$JAVA/adapter/in/rest/CustomCardController.java"

# Adapter out - messaging
touch "$JAVA/adapter/out/messaging/CardCreatedEvent.java"
touch "$JAVA/adapter/out/messaging/CardValidatedEvent.java"
touch "$JAVA/adapter/out/messaging/CardEventPublisher.java"
touch "$JAVA/adapter/out/messaging/CardValidationConsumer.java"

# Adapter out - persistence
touch "$JAVA/adapter/out/persistence/entity/CustomCardEntity.java"
touch "$JAVA/adapter/out/persistence/repository/CustomCardJpaRepository.java"
touch "$JAVA/adapter/out/persistence/CustomCardRepositoryAdapter.java"

echo "Estrutura do card-creator-service criada com sucesso!"
