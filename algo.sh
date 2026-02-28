#!/bin/bash

BASE="shared-domain"
JAVA="$BASE/src/main/java/com/odevpedro/yugiohcollections/shared/enums"

mkdir -p "$JAVA"

touch "$BASE/build.gradle"
touch "$BASE/settings.gradle"
touch "$JAVA/CardType.java"
touch "$JAVA/MonsterAttribute.java"
touch "$JAVA/MonsterType.java"
touch "$JAVA/MonsterSubType.java"
touch "$JAVA/SpellType.java"
touch "$JAVA/TrapType.java"

echo "Estrutura do shared-domain criada com sucesso!"
