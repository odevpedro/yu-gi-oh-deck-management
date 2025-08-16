-- SPELL: sem duplicar a mesma carta para o mesmo dono
ALTER TABLE spell_cards
  ADD CONSTRAINT uk_spell_owner_name_type_st
  UNIQUE (owner_id, name, type, spell_type);

-- TRAP
ALTER TABLE trap_cards
  ADD CONSTRAINT uk_trap_owner_name_type_tt
  UNIQUE (owner_id, name, type, trap_type);

-- MONSTER (mínimo); ajuste se quiser mais campos
ALTER TABLE monster_cards
  ADD CONSTRAINT uk_mon_owner_name_type
  UNIQUE (owner_id, name, type);
